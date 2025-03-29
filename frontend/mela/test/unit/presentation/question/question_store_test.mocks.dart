// Mocks generated by Mockito 5.4.4 from annotations
// in mela/test/unit/presentation/question/question_store_test.dart.
// Do not manually edit this file.

// ignore_for_file: no_leading_underscores_for_library_prefixes
import 'dart:async' as _i4;

import 'package:mela/core/stores/error/error_store.dart' as _i8;
import 'package:mela/domain/entity/question/question_list.dart' as _i5;
import 'package:mela/domain/params/question/submit_result_params.dart' as _i7;
import 'package:mela/domain/usecase/question/get_questions_usecase.dart' as _i3;
import 'package:mela/domain/usecase/question/submit_result_usecase.dart' as _i6;
import 'package:mobx/mobx.dart' as _i2;
import 'package:mockito/mockito.dart' as _i1;
import 'package:mockito/src/dummies.dart' as _i9;

// ignore_for_file: type=lint
// ignore_for_file: avoid_redundant_argument_values
// ignore_for_file: avoid_setters_without_getters
// ignore_for_file: comment_references
// ignore_for_file: deprecated_member_use
// ignore_for_file: deprecated_member_use_from_same_package
// ignore_for_file: implementation_imports
// ignore_for_file: invalid_use_of_visible_for_testing_member
// ignore_for_file: prefer_const_constructors
// ignore_for_file: unnecessary_parenthesis
// ignore_for_file: camel_case_types
// ignore_for_file: subtype_of_sealed_class

class _FakeReactiveContext_0 extends _i1.SmartFake
    implements _i2.ReactiveContext {
  _FakeReactiveContext_0(
    Object parent,
    Invocation parentInvocation,
  ) : super(
          parent,
          parentInvocation,
        );
}

/// A class which mocks [GetQuestionsUseCase].
///
/// See the documentation for Mockito's code generation for more information.
class MockGetQuestionsUseCase extends _i1.Mock
    implements _i3.GetQuestionsUseCase {
  MockGetQuestionsUseCase() {
    _i1.throwOnMissingStub(this);
  }

  @override
  _i4.Future<_i5.QuestionList?> call({required String? params}) =>
      (super.noSuchMethod(
        Invocation.method(
          #call,
          [],
          {#params: params},
        ),
        returnValue: _i4.Future<_i5.QuestionList?>.value(),
      ) as _i4.Future<_i5.QuestionList?>);
}

/// A class which mocks [SubmitResultUseCase].
///
/// See the documentation for Mockito's code generation for more information.
class MockSubmitResultUseCase extends _i1.Mock
    implements _i6.SubmitResultUseCase {
  MockSubmitResultUseCase() {
    _i1.throwOnMissingStub(this);
  }

  @override
  _i4.Future<int> call({required _i7.SubmitResultParams? params}) =>
      (super.noSuchMethod(
        Invocation.method(
          #call,
          [],
          {#params: params},
        ),
        returnValue: _i4.Future<int>.value(0),
      ) as _i4.Future<int>);
}

/// A class which mocks [ErrorStore].
///
/// See the documentation for Mockito's code generation for more information.
class MockErrorStore extends _i1.Mock implements _i8.ErrorStore {
  MockErrorStore() {
    _i1.throwOnMissingStub(this);
  }

  @override
  String get errorMessage => (super.noSuchMethod(
        Invocation.getter(#errorMessage),
        returnValue: _i9.dummyValue<String>(
          this,
          Invocation.getter(#errorMessage),
        ),
      ) as String);

  @override
  set errorMessage(String? value) => super.noSuchMethod(
        Invocation.setter(
          #errorMessage,
          value,
        ),
        returnValueForMissingStub: null,
      );

  @override
  _i2.ReactiveContext get context => (super.noSuchMethod(
        Invocation.getter(#context),
        returnValue: _FakeReactiveContext_0(
          this,
          Invocation.getter(#context),
        ),
      ) as _i2.ReactiveContext);

  @override
  void setErrorMessage(String? message) => super.noSuchMethod(
        Invocation.method(
          #setErrorMessage,
          [message],
        ),
        returnValueForMissingStub: null,
      );

  @override
  void reset(String? value) => super.noSuchMethod(
        Invocation.method(
          #reset,
          [value],
        ),
        returnValueForMissingStub: null,
      );
}
