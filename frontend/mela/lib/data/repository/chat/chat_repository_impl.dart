import 'package:mela/data/network/apis/chat/chat_api.dart';
import 'package:mela/domain/entity/message_chat/conversation.dart';
import 'package:mela/domain/entity/message_chat/message_chat.dart';
import 'package:mela/domain/repository/chat/chat_repository.dart';
import 'package:mela/domain/usecase/chat/create_new_conversation_usecase.dart';
import 'package:mela/domain/usecase/chat/get_conversation_usecase.dart';
import 'package:mela/domain/usecase/chat/send_message_chat_usecase.dart';

class ChatRepositoryImpl extends ChatRepository {
  ChatApi _chatApi;
  ChatRepositoryImpl(this._chatApi);
  @override
  Future<Conversation> getConversation(GetConversationRequestParams params) {
    return _chatApi.getConversation(params);
  }

  @override
  Future<MessageChat> sendMessage(ChatRequestParams params) {
    return _chatApi.sendMessageChat(params);
  }

  @override
  Future<Conversation> createNewConversation(
      CreateNewConversationParams params) {
    return _chatApi.createNewConversation(params);
  }
}
