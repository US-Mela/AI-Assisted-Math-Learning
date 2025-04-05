import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter_mobx/flutter_mobx.dart';
import 'package:image_cropper/image_cropper.dart';
import 'package:image_picker/image_picker.dart';
import 'package:mela/constants/app_theme.dart';
import 'package:mela/constants/enum.dart';
// import 'package:math_keyboard/math_keyboard.dart';
import 'package:mela/di/service_locator.dart';
import 'package:mela/domain/entity/message_chat/conversation.dart';
import 'package:mela/presentation/thread_chat/store/chat_box_store/chat_box_store.dart';
import 'package:mela/presentation/thread_chat/store/thread_chat_store/thread_chat_store.dart';
import 'package:mela/utils/image_picker_helper/image_picker_helper.dart';
import 'package:mela/utils/routes/routes.dart';
import 'package:mobx/mobx.dart';

class ChatBox extends StatefulWidget {
  bool isFirstChatScreen;
  ChatBox({super.key, this.isFirstChatScreen = false});

  @override
  _ChatBoxState createState() => _ChatBoxState();
}

class _ChatBoxState extends State<ChatBox> {
  final TextEditingController _controller = TextEditingController();
  // late final _controllerMath = MathFieldEditingController();
  // bool _isMathMode = false;
  final _chatBoxStore = getIt.get<ChatBoxStore>();
  final _threadChatStore = getIt.get<ThreadChatStore>();
  final FocusNode _focusNode = FocusNode();
  ValueNotifier<List<File>> _imagesNotifier = ValueNotifier<List<File>>([]);
  final ImagePickerHelper _imagePickerHelper = ImagePickerHelper();
  late ReactionDisposer disposerIsDisplayCamera;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _chatBoxStore.setShowSendIcon(false);
      _chatBoxStore.setShowCameraIcon(true);
    });
    _controller.addListener(onTextChanged);
    _focusNode.addListener(() {
      setState(() {}); // Rebuild when focus changes
    });
    disposerIsDisplayCamera = reaction<Conversation>(
      (_) => _threadChatStore.currentConversation,
      (value) {
        if (value.levelConversation == LevelConversation.PROBLEM_IDENTIFIED) {
          print("Current Conversation thay doi thanh PROBLEM_IDENTIFIED");
          _chatBoxStore.setShowCameraIcon(false);
        } else if (value.levelConversation == LevelConversation.UNIDENTIFIED) {
          print("Current Conversation thay doi thanh UNIDENTIFIED");
          _chatBoxStore.setShowCameraIcon(true);
        } else {
          print("Current Conversation Level -----> ${value.levelConversation}");
        }
      },
    );
  }

  @override
  void dispose() {
    _controller.removeListener(onTextChanged);
    _controller.dispose();
    _focusNode.dispose();
    disposerIsDisplayCamera();

    super.dispose();
  }

  //-----------------------Function
  //Event to display/hide the accessibility icons
  void onTextChanged() {
    if ((_controller.text.isNotEmpty || _imagesNotifier.value.isNotEmpty) &&
        _threadChatStore.isLoading == false) {
      _chatBoxStore.setShowSendIcon(true);
    } else {
      _chatBoxStore.setShowSendIcon(false);
    }
  }

  //Remove Image in the list
  void _removeImage(File image) {
    _imagesNotifier.value.remove(image);

    //Must update the value to notify the listeners
    _imagesNotifier.value = _imagesNotifier.value.toList();

    if (_imagesNotifier.value.isEmpty && _controller.text.isEmpty) {
      _chatBoxStore.setShowSendIcon(false);
    }
  }

  Future<void> pickImage(ImageSource imageSource) async {
    XFile? image = await _imagePickerHelper.pickImageFromGalleryOrCamera(
        source: imageSource);

    if (image != null) {
      // File imageFile = File(image.path);
      // _imagesNotifier.value = [imageFile];
      CroppedFile? croppedFile = await _imagePickerHelper.cropImage(image);
      if (croppedFile == null) return;
      File imageFile = File(croppedFile.path);
      _imagesNotifier.value = [imageFile];
      _chatBoxStore.setShowSendIcon(true);
    }
  }

  Future<void> pickMultiImage() async {
    List<XFile> images = await _imagePickerHelper.pickMultipleImages();
    if (images.isNotEmpty) {
      if (images.length == 1) {
        CroppedFile? croppedFile =
            await _imagePickerHelper.cropImage(images[0]);
        if (croppedFile == null) return;
        File imageFile = File(croppedFile.path);
        _imagesNotifier.value = [imageFile];
        return;
      }
      List<File> imageFiles = images.map((image) => File(image.path)).toList();
      _imagesNotifier.value = imageFiles;
    }
  }

  void _showImagePickerOptions() {
    showModalBottomSheet(
      context: context,
      builder: (BuildContext context) {
        return Wrap(
          children: [
            ListTile(
              leading: const Icon(Icons.camera_alt),
              title: const Text("Chụp ảnh mới"),
              onTap: () {
                Navigator.pop(context);
                pickImage(ImageSource.camera);
              },
            ),
            ListTile(
              leading: const Icon(Icons.photo_library),
              title: const Text("Chọn ảnh từ thư viện"),
              onTap: () {
                Navigator.pop(context);
                pickImage(ImageSource.gallery);
              },
            ),
          ],
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Focus(
      focusNode: _focusNode,
      child: GestureDetector(
        onTap: () => _focusNode.requestFocus(),
        child: Container(
          margin: widget.isFirstChatScreen
              ? null
              : const EdgeInsets.fromLTRB(5, 0, 5, 5),
          padding: const EdgeInsets.symmetric(vertical: 8),
          decoration: BoxDecoration(
            color: Colors.white,
            border: _focusNode.hasFocus
                ? Border.all(
                    color: Theme.of(context).colorScheme.buttonYesBgOrText,
                    strokeAlign: BorderSide.strokeAlignOutside,
                    width: 1.5)
                : null,
            borderRadius: const BorderRadius.all(Radius.circular(15)),
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              //Images when choose from library
              _buildListImagesIsSeclected(),

              //TextField
              _buildTextField(),
              const SizedBox(height: 5),

              //Support Icons
              // _buildSupportIcons(),
            ],
          ),
        ),
      ),
    );
  }

  //-------------------------------Widget---------------
  Widget _buildListImagesIsSeclected() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
      child: ValueListenableBuilder(
          valueListenable: _imagesNotifier,
          builder: (context, images, child) {
            return images.isEmpty
                ? const SizedBox()
                : SingleChildScrollView(
                    scrollDirection: Axis.horizontal,
                    child: Row(
                      // mainAxisAlignment: MainAxisAlignment.start,
                      // mainAxisSize: MainAxisSize.min,
                      children: [
                        ...images.map((image) {
                          return Padding(
                            padding: const EdgeInsets.only(right: 4),
                            child: Stack(
                              children: [
                                // Image container
                                Container(
                                  width: 60,
                                  height: 60,
                                  decoration: BoxDecoration(
                                    borderRadius: BorderRadius.circular(8),
                                    border: Border.all(width: 0),
                                    image: DecorationImage(
                                      image: FileImage(image),
                                      fit: BoxFit.cover,
                                    ),
                                  ),
                                ),
                                // Close button
                                Positioned(
                                  top: 3,
                                  right: 3,
                                  child: Material(
                                    color: Colors.transparent,
                                    child: InkWell(
                                      borderRadius: BorderRadius.circular(50),
                                      focusColor: Colors.amber,
                                      onTap: () {
                                        _removeImage(image);
                                      },
                                      child: Ink(
                                        decoration: BoxDecoration(
                                          color: Colors.grey,
                                          borderRadius:
                                              BorderRadius.circular(50),
                                        ),
                                        width: 20,
                                        height: 20,
                                        child: const Icon(
                                          Icons.close,
                                          color: Colors.white,
                                          size: 20,
                                        ),
                                      ),
                                    ),
                                  ),
                                ),
                              ],
                            ),
                          );
                        }).toList(),
                      ],
                    ),
                  );
          }),
    );
  }

  Widget _buildTextField() {
    return Observer(builder: (_) {
      return Row(
        crossAxisAlignment: CrossAxisAlignment.end,
        children: [
          _chatBoxStore.showCameraIcon
              ? GestureDetector(
                  onTap: _showImagePickerOptions,
                  child: Padding(
                    padding:
                        const EdgeInsets.only(left: 6.0, top: 4, bottom: 4),
                    child: Icon(Icons.image,
                        color: Theme.of(context)
                            .colorScheme
                            .buttonYesBgOrText
                            .withOpacity(0.8),
                        size: 24),
                  ),
                )
              : const SizedBox(),
          Expanded(
            child: Container(
              padding:
                  const EdgeInsets.only(right: 4, top: 4, bottom: 4, left: 6),
              child: TextField(
                controller: _controller,
                maxLines: 3,
                minLines: 1,
                style: const TextStyle(
                  color: Colors.black,
                  fontSize: 16,
                ),
                textAlignVertical: TextAlignVertical.bottom,
                decoration: const InputDecoration(
                  contentPadding: EdgeInsets.zero,
                  isDense: true,
                  hintText: "Hãy cho Mela biết thắc mắc của bạn",
                  hintStyle: TextStyle(
                    color: Colors.grey,
                    fontSize: 16,
                  ),
                  border: InputBorder.none,
                ),
              ),
            ),
          ),
          _chatBoxStore.showSendIcon
              ? GestureDetector(
                  onTap: () async {
                    if (widget.isFirstChatScreen) {
                      _threadChatStore.setConversation(Conversation(
                          conversationId: "",
                          messages: [],
                          hasMore: false,
                          levelConversation: LevelConversation.UNIDENTIFIED,
                          dateConversation: DateTime.now(),
                          nameConversation: ""));

                      Navigator.of(context).pushNamed(Routes.threadChatScreen);
                    }

                    String message = _controller.text.trim();
                    _controller.clear();
                    _chatBoxStore.setShowSendIcon(false);

                    // Hide keyboard
                    if (_focusNode.hasFocus) {
                      _focusNode.unfocus();
                    }

                    List<File> images = _imagesNotifier.value.toList();
                    _imagesNotifier.value = [];
                    await _threadChatStore.sendChatMessage(message, images);

                    //Using for while loading response user enter new message availale
                    if (_controller.text.isNotEmpty ||
                        _imagesNotifier.value.isNotEmpty) {
                      _chatBoxStore.setShowSendIcon(true);
                    }
                  },
                  child: Padding(
                    padding:
                        const EdgeInsets.symmetric(vertical: 4, horizontal: 4),
                    child: Icon(Icons.send,
                        color: Theme.of(context).colorScheme.buttonYesBgOrText,
                        size: 24),
                  ),
                )
              : const SizedBox(),
        ],
      );
    });
  }

  Widget _buildSupportIcons() {
    return Padding(
      padding: const EdgeInsets.fromLTRB(10, 0, 10, 6),
      child: Row(
        children: [
          Expanded(
            child: Row(
              children: [
                GestureDetector(
                  onTap: () {},
                  child: Icon(
                    Icons.functions,
                    color: Theme.of(context)
                        .colorScheme
                        .buttonYesBgOrText
                        .withOpacity(0.8),
                    size: 24,
                  ),
                ),
                const SizedBox(width: 10),
                GestureDetector(
                  onTap: () => pickImage(ImageSource.camera),
                  child: Icon(Icons.camera_alt,
                      color: Theme.of(context)
                          .colorScheme
                          .buttonYesBgOrText
                          .withOpacity(0.8),
                      size: 24),
                ),
                const SizedBox(width: 10),
                GestureDetector(
                  onTap: () => pickMultiImage(),
                  child: Icon(Icons.image,
                      color: Theme.of(context)
                          .colorScheme
                          .buttonYesBgOrText
                          .withOpacity(0.8),
                      size: 24),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
