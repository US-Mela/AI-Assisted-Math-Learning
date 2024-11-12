// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'single_question_store.dart';

// **************************************************************************
// StoreGenerator
// **************************************************************************

// ignore_for_file: non_constant_identifier_names, unnecessary_brace_in_string_interps, unnecessary_lambdas, prefer_expression_function_bodies, lines_longer_than_80_chars, avoid_as, avoid_annotating_with_dynamic, no_leading_underscores_for_local_identifiers

mixin _$SingleQuestionStore on _SingleQuestionStore, Store {
  Computed<String>? _$currentAnswerComputed;

  @override
  String get currentAnswer =>
      (_$currentAnswerComputed ??= Computed<String>(() => super.currentAnswer,
              name: '_SingleQuestionStore.currentAnswer'))
          .value;
  Computed<List<String>>? _$userAnswersComputed;

  @override
  List<String> get userAnswers =>
      (_$userAnswersComputed ??= Computed<List<String>>(() => super.userAnswers,
              name: '_SingleQuestionStore.userAnswers'))
          .value;
  Computed<int>? _$currentIndexComputed;

  @override
  int get currentIndex =>
      (_$currentIndexComputed ??= Computed<int>(() => super.currentIndex,
              name: '_SingleQuestionStore.currentIndex'))
          .value;

  late final _$_questionIndexAtom =
      Atom(name: '_SingleQuestionStore._questionIndex', context: context);

  @override
  int get _questionIndex {
    _$_questionIndexAtom.reportRead();
    return super._questionIndex;
  }

  @override
  set _questionIndex(int value) {
    _$_questionIndexAtom.reportWrite(value, super._questionIndex, () {
      super._questionIndex = value;
    });
  }

  late final _$_userAnswersAtom =
      Atom(name: '_SingleQuestionStore._userAnswers', context: context);

  @override
  List<String> get _userAnswers {
    _$_userAnswersAtom.reportRead();
    return super._userAnswers;
  }

  @override
  set _userAnswers(List<String> value) {
    _$_userAnswersAtom.reportWrite(value, super._userAnswers, () {
      super._userAnswers = value;
    });
  }

  late final _$currentQuizAnswerAtom =
      Atom(name: '_SingleQuestionStore.currentQuizAnswer', context: context);

  @override
  String get currentQuizAnswer {
    _$currentQuizAnswerAtom.reportRead();
    return super.currentQuizAnswer;
  }

  @override
  set currentQuizAnswer(String value) {
    _$currentQuizAnswerAtom.reportWrite(value, super.currentQuizAnswer, () {
      super.currentQuizAnswer = value;
    });
  }

  late final _$_SingleQuestionStoreActionController =
      ActionController(name: '_SingleQuestionStore', context: context);

  @override
  void generateAnswerList(int length) {
    final _$actionInfo = _$_SingleQuestionStoreActionController.startAction(
        name: '_SingleQuestionStore.generateAnswerList');
    try {
      return super.generateAnswerList(length);
    } finally {
      _$_SingleQuestionStoreActionController.endAction(_$actionInfo);
    }
  }

  @override
  void changeQuestion(int index) {
    final _$actionInfo = _$_SingleQuestionStoreActionController.startAction(
        name: '_SingleQuestionStore.changeQuestion');
    try {
      return super.changeQuestion(index);
    } finally {
      _$_SingleQuestionStoreActionController.endAction(_$actionInfo);
    }
  }

  @override
  void setAnswer(int index, String userAnswer) {
    final _$actionInfo = _$_SingleQuestionStoreActionController.startAction(
        name: '_SingleQuestionStore.setAnswer');
    try {
      return super.setAnswer(index, userAnswer);
    } finally {
      _$_SingleQuestionStoreActionController.endAction(_$actionInfo);
    }
  }

  @override
  void setQuizAnswerValue(String value) {
    final _$actionInfo = _$_SingleQuestionStoreActionController.startAction(
        name: '_SingleQuestionStore.setQuizAnswerValue');
    try {
      return super.setQuizAnswerValue(value);
    } finally {
      _$_SingleQuestionStoreActionController.endAction(_$actionInfo);
    }
  }

  @override
  String toString() {
    return '''
currentQuizAnswer: ${currentQuizAnswer},
currentAnswer: ${currentAnswer},
userAnswers: ${userAnswers},
currentIndex: ${currentIndex}
    ''';
  }
}
