import 'package:mela/domain/entity/question/question.dart';

import '../../entity/question/question_list.dart';


abstract class QuestionRepository{
  Future<QuestionList> getQuestions();
  Future<QuestionList> updateQuestions(QuestionList newQuestionList);
}