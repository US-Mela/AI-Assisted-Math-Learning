// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'topic_store.dart';

// **************************************************************************
// StoreGenerator
// **************************************************************************

// ignore_for_file: non_constant_identifier_names, unnecessary_brace_in_string_interps, unnecessary_lambdas, prefer_expression_function_bodies, lines_longer_than_80_chars, avoid_as, avoid_annotating_with_dynamic, no_leading_underscores_for_local_identifiers

mixin _$TopicStore on _TopicStore, Store {
  Computed<bool>? _$loadingComputed;

  @override
  bool get loading => (_$loadingComputed ??=
          Computed<bool>(() => super.loading, name: '_TopicStore.loading'))
      .value;

  late final _$topicListAtom =
      Atom(name: '_TopicStore.topicList', context: context);

  @override
  TopicList? get topicList {
    _$topicListAtom.reportRead();
    return super.topicList;
  }

  @override
  set topicList(TopicList? value) {
    _$topicListAtom.reportWrite(value, super.topicList, () {
      super.topicList = value;
    });
  }

  late final _$lecturesAreLearningListAtom =
      Atom(name: '_TopicStore.lecturesAreLearningList', context: context);

  @override
  LectureList? get lecturesAreLearningList {
    _$lecturesAreLearningListAtom.reportRead();
    return super.lecturesAreLearningList;
  }

  @override
  set lecturesAreLearningList(LectureList? value) {
    _$lecturesAreLearningListAtom
        .reportWrite(value, super.lecturesAreLearningList, () {
      super.lecturesAreLearningList = value;
    });
  }

  late final _$levelListAtom =
      Atom(name: '_TopicStore.levelList', context: context);

  @override
  LevelList? get levelList {
    _$levelListAtom.reportRead();
    return super.levelList;
  }

  @override
  set levelList(LevelList? value) {
    _$levelListAtom.reportWrite(value, super.levelList, () {
      super.levelList = value;
    });
  }

  late final _$errorStringAtom =
      Atom(name: '_TopicStore.errorString', context: context);

  @override
  String get errorString {
    _$errorStringAtom.reportRead();
    return super.errorString;
  }

  @override
  set errorString(String value) {
    _$errorStringAtom.reportWrite(value, super.errorString, () {
      super.errorString = value;
    });
  }

  late final _$isUnAuthorizedAtom =
      Atom(name: '_TopicStore.isUnAuthorized', context: context);

  @override
  bool get isUnAuthorized {
    _$isUnAuthorizedAtom.reportRead();
    return super.isUnAuthorized;
  }

  @override
  set isUnAuthorized(bool value) {
    _$isUnAuthorizedAtom.reportWrite(value, super.isUnAuthorized, () {
      super.isUnAuthorized = value;
    });
  }

  late final _$fetchTopicsFutureAtom =
      Atom(name: '_TopicStore.fetchTopicsFuture', context: context);

  @override
  ObservableFuture<TopicList?> get fetchTopicsFuture {
    _$fetchTopicsFutureAtom.reportRead();
    return super.fetchTopicsFuture;
  }

  @override
  set fetchTopicsFuture(ObservableFuture<TopicList?> value) {
    _$fetchTopicsFutureAtom.reportWrite(value, super.fetchTopicsFuture, () {
      super.fetchTopicsFuture = value;
    });
  }

  late final _$fetchLecturesAreLearningFutureAtom = Atom(
      name: '_TopicStore.fetchLecturesAreLearningFuture', context: context);

  @override
  ObservableFuture<LectureList?> get fetchLecturesAreLearningFuture {
    _$fetchLecturesAreLearningFutureAtom.reportRead();
    return super.fetchLecturesAreLearningFuture;
  }

  @override
  set fetchLecturesAreLearningFuture(ObservableFuture<LectureList?> value) {
    _$fetchLecturesAreLearningFutureAtom
        .reportWrite(value, super.fetchLecturesAreLearningFuture, () {
      super.fetchLecturesAreLearningFuture = value;
    });
  }

  late final _$fetchLevelsFutureAtom =
      Atom(name: '_TopicStore.fetchLevelsFuture', context: context);

  @override
  ObservableFuture<LevelList?> get fetchLevelsFuture {
    _$fetchLevelsFutureAtom.reportRead();
    return super.fetchLevelsFuture;
  }

  @override
  set fetchLevelsFuture(ObservableFuture<LevelList?> value) {
    _$fetchLevelsFutureAtom.reportWrite(value, super.fetchLevelsFuture, () {
      super.fetchLevelsFuture = value;
    });
  }

  late final _$getTopicsAsyncAction =
      AsyncAction('_TopicStore.getTopics', context: context);

  @override
  Future<dynamic> getTopics() {
    return _$getTopicsAsyncAction.run(() => super.getTopics());
  }

  late final _$_TopicStoreActionController =
      ActionController(name: '_TopicStore', context: context);

  @override
  void resetErrorString() {
    final _$actionInfo = _$_TopicStoreActionController.startAction(
        name: '_TopicStore.resetErrorString');
    try {
      return super.resetErrorString();
    } finally {
      _$_TopicStoreActionController.endAction(_$actionInfo);
    }
  }

  @override
  String toString() {
    return '''
topicList: ${topicList},
lecturesAreLearningList: ${lecturesAreLearningList},
levelList: ${levelList},
errorString: ${errorString},
isUnAuthorized: ${isUnAuthorized},
fetchTopicsFuture: ${fetchTopicsFuture},
fetchLecturesAreLearningFuture: ${fetchLecturesAreLearningFuture},
fetchLevelsFuture: ${fetchLevelsFuture},
loading: ${loading}
    ''';
  }
}
