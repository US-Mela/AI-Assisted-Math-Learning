import 'package:flutter/material.dart';
import 'package:mela/models/QuestionFamilly/AQuestion.dart';
import 'package:mela/models/QuestionFamilly/FitbQuestion.dart';

class Global {
  static Color AppBackgroundColor = const Color(0xFFF5F9FF);
  static Color AppBarContentColor = const Color(0xFF202244);
  static double PracticeLeftPadding = 15;
  static double PracticeRightPadding = 34;

  static List<AQuestion> questions = [
    FitbQuestion(
        id: 'FQ01',
        questionContent: 'Trong một buổi tiệc, có 12 người tham gia. Mỗi người đều bắt tay với mỗi người còn lại một lần. Hỏi tổng số lần bắt tay diễn ra là bao nhiêu?',
        answer: 'true',
        imageUrl: null),
  ];
}