import 'dart:async';

import 'package:mela/core/domain/usecase/use_case.dart';
import 'package:mela/domain/entity/topic/topic_list.dart';

import '../../repository/topic/topic_repository.dart';

class GetTopicsUsecase extends UseCase<TopicList, void> {
  final TopicRepository _topicRepository;

  GetTopicsUsecase(this._topicRepository);

  //params is type dynamic so it can pass by null
  @override
  Future<TopicList> call({required void params}) async {
    return _topicRepository.getTopics();
  }
}
