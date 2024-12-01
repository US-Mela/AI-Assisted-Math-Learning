import 'package:dio/dio.dart';
import 'package:mela/domain/entity/question/question_list.dart';
import 'package:mela/domain/repository/question/question_repository.dart';

import '../../network/apis/questions/questions_api.dart';

class QuestionRepositoryImpl extends QuestionRepository{
  final QuestionsApi _questionsApi;

  QuestionRepositoryImpl(this._questionsApi);

  @override
  Future<QuestionList> getQuestions(String exerciseUid) async{
    try {
      QuestionList temp = await _questionsApi.getQuestions(exerciseUid);
      return temp;
    }
    catch (e){
      rethrow;
    }
  }

  @override
  Future<QuestionList> updateQuestions(QuestionList newQuestionList) {
    // TODO: implement updateQuestions
    throw UnimplementedError();
  }
}