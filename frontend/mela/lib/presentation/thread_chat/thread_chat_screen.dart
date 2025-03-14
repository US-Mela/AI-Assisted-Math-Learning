import 'package:flutter/material.dart';
import 'package:flutter_mobx/flutter_mobx.dart';
import 'package:loading_animation_widget/loading_animation_widget.dart';
import 'package:mela/constants/app_theme.dart';
import 'package:mela/core/widgets/image_progress_indicator.dart';
import 'package:mela/di/service_locator.dart';
import 'package:mela/presentation/thread_chat/store/thread_chat_store/thread_chat_store.dart';
import 'package:mela/presentation/thread_chat/widgets/chat_box.dart';
import 'package:mela/presentation/thread_chat/widgets/message_chat_title.dart';
import 'package:mobx/mobx.dart';

class ThreadChatScreen extends StatefulWidget {
  ThreadChatScreen({super.key});

  @override
  State<ThreadChatScreen> createState() => _ThreadChatScreenState();
}

class _ThreadChatScreenState extends State<ThreadChatScreen> {
  final ThreadChatStore _threadChatStore = getIt.get<ThreadChatStore>();
  final ScrollController _scrollController = ScrollController();
  late ReactionDisposer disposerSendMessage;
  late ReactionDisposer disposerGetConversation;

  @override
  void initState() {
    super.initState();
    _scrollController.addListener(_onScroll);

    //For first time go to from history it will scroll to bottom
    disposerGetConversation = reaction<bool>(
      (_) => _threadChatStore.isLoadingGetConversation,
      (value) {
        if (!value) {
          WidgetsBinding.instance
              .addPostFrameCallback((_) => _scrollToBottom());
        }
      },
    );
    //For send message and get response from AI, it will scroll to bottom
    disposerSendMessage = reaction<bool>(
      (_) => _threadChatStore.isLoading,
      (value) {
        //not need to check value ==true because it must always loading when isLoading change
        WidgetsBinding.instance.addPostFrameCallback((_) => _scrollToBottom());
      },
    );
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    _threadChatStore.getConversation();
  }

  @override
  void dispose() {
    disposerSendMessage();
    disposerGetConversation();
    _scrollController.dispose();
    super.dispose();
  }

//For scroll at the top and loading get older messages
  void _onScroll() {
    if (_scrollController.position.pixels <= 0 &&
        !_threadChatStore.isLoadingGetConversation &&
        _threadChatStore.currentConversation.hasMore) {
      print("=======================>On Scroll At the top");
      _threadChatStore.getOlderMessages();
    }
  }

  void _scrollToBottom() {
    if (_scrollController.hasClients) {
      _scrollController.animateTo(
        _scrollController.position.maxScrollExtent,
        duration: const Duration(milliseconds: 300),
        curve: Curves.fastEaseInToSlowEaseOut,
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        scrolledUnderElevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back, color: Colors.black),
          onPressed: () {
            _threadChatStore.clearConversation();
            Navigator.of(context).pop();
          },
        ),
        actions: [
          Observer(builder: (context) {
            return IconButton(
              onPressed: _threadChatStore.isLoadingGetConversation
                  ? null
                  : () {
                      _threadChatStore.clearConversation();
                    },
              icon: Icon(
                Icons.add_circle_outline,
                color: Theme.of(context).colorScheme.buttonYesBgOrText,
              ),
            );
          })
        ],
        title: Observer(builder: (context) {
          return Text(
            _threadChatStore.conversationName,
            style: Theme.of(context)
                .textTheme
                .heading
                .copyWith(color: Theme.of(context).colorScheme.primary),
          );
        }),
      ),
      body: Observer(builder: (context) {
        return _threadChatStore.isLoadingGetConversation
            ? AbsorbPointer(
                absorbing: true,
                child: Stack(
                  alignment: Alignment.center,
                  children: [
                    Container(
                      color: Theme.of(context)
                          .colorScheme
                          .surface
                          .withOpacity(0.8),
                    ),
                    const RotatingImageIndicator(),
                  ],
                ),
              )
            : Column(
                children: [
                  Expanded(
                    child: _threadChatStore.currentConversation.messages.isEmpty
                        ? const Center(
                            child: Text("Start a conversation"),
                          )
                        : ScrollbarTheme(
                            data: ScrollbarThemeData(
                              thumbColor:
                                  MaterialStateProperty.all(Colors.grey),
                              trackColor:
                                  MaterialStateProperty.all(Colors.yellow),
                              radius: const Radius.circular(20),
                              thickness: MaterialStateProperty.all(4),
                            ),
                            child: Scrollbar(
                              child: SingleChildScrollView(
                                //Must use SingleChildScrollView
                                controller: _scrollController,
                                child: Column(children: [
                                  if (_threadChatStore
                                      .isLoadingGetOlderMessages) ...[
                                    Padding(
                                      padding: const EdgeInsets.symmetric(
                                          vertical: 12),
                                      child: Row(
                                        mainAxisAlignment:
                                            MainAxisAlignment.center,
                                        children: [
                                          LoadingAnimationWidget
                                              .staggeredDotsWave(
                                            color: Theme.of(context)
                                                .colorScheme
                                                .buttonYesBgOrText,
                                            size: 34,
                                          )
                                        ],
                                      ),
                                    )
                                  ],
                                  ..._threadChatStore
                                      .currentConversation.messages
                                      .map((message) => MessageChatTitle(
                                          currentMessage: message))
                                      .toList()
                                ]),
                              ),
                            ),
                          ),
                  ),
                  ChatBox()
                ],
              );
      }),
    );
  }
}
