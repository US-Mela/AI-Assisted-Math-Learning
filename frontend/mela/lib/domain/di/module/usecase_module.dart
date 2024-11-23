import 'dart:async';


import 'package:mela/domain/repository/lecture/lecture_repository.dart';
import 'package:mela/domain/repository/user_register/user_signup_repostiory.dart';
import 'package:mela/domain/usecase/exercise/get_exercises_usecase.dart';
import 'package:mela/domain/usecase/lecture/get_lectures_usecase.dart';
import 'package:mela/domain/usecase/search/get_search_lectures_result.dart';
import 'package:mela/domain/usecase/topic/find_topic_by_id_usecase.dart';
import 'package:mela/domain/usecase/topic/get_topics_usecase.dart';

import '../../../di/service_locator.dart';
import '../../repository/exercise/exercise_repository.dart';
import '../../repository/post/post_repository.dart';
import '../../repository/search/search_repository.dart';
import '../../repository/topic/topic_repository.dart';
import '../../repository/user_login/user_login_repository.dart';
import '../../usecase/lecture/get_lectures_are_learning_usecase.dart';
import '../../usecase/post/delete_post_usecase.dart';
import '../../usecase/post/find_post_by_id_usecase.dart';
import '../../usecase/post/get_post_usecase.dart';
import '../../usecase/post/insert_post_usecase.dart';
import '../../usecase/post/udpate_post_usecase.dart';
import '../../usecase/search/get_history_search_list.dart';
import '../../usecase/user_login/is_logged_in_usecase.dart';
import '../../usecase/user_login/login_usecase.dart';
import '../../usecase/user_login/save_login_in_status_usecase.dart';
import '../../usecase/user_signup/signup_usecase.dart';

class UseCaseModule {
  static Future<void> configureUseCaseModuleInjection() async {
    // user login:--------------------------------------------------------------------
    getIt.registerSingleton<IsLoggedInUseCase>(
      IsLoggedInUseCase(getIt<UserLoginRepository>()),
    );
    getIt.registerSingleton<SaveLoginStatusUseCase>(
      SaveLoginStatusUseCase(getIt<UserLoginRepository>()),
    );
    getIt.registerSingleton<LoginUseCase>(
      LoginUseCase(getIt<UserLoginRepository>()),
    );
    //user signup:--------------------------------------------------------------------
    getIt.registerSingleton<SignupUseCase>(
      SignupUseCase(getIt<UserSignUpRepository>()),
import 'package:mela/domain/repository/post/post_repository.dart';
import 'package:mela/domain/repository/question/question_repository.dart';
import 'package:mela/domain/repository/user/user_repository.dart';
import 'package:mela/domain/usecase/post/delete_post_usecase.dart';
import 'package:mela/domain/usecase/post/find_post_by_id_usecase.dart';
import 'package:mela/domain/usecase/post/get_post_usecase.dart';
import 'package:mela/domain/usecase/post/insert_post_usecase.dart';
import 'package:mela/domain/usecase/post/udpate_post_usecase.dart';
import 'package:mela/domain/usecase/question/get_questions_usecase.dart';
import 'package:mela/domain/usecase/user/is_logged_in_usecase.dart';
import 'package:mela/domain/usecase/user/login_usecase.dart';
import 'package:mela/domain/usecase/user/save_login_in_status_usecase.dart';

import '../../../di/service_locator.dart';

class UseCaseModule {
  static Future<void> configureUseCaseModuleInjection() async {
    // user:--------------------------------------------------------------------
    getIt.registerSingleton<IsLoggedInUseCase>(
      IsLoggedInUseCase(getIt<UserRepository>()),
    );
    getIt.registerSingleton<SaveLoginStatusUseCase>(
      SaveLoginStatusUseCase(getIt<UserRepository>()),
    );
    getIt.registerSingleton<LoginUseCase>(
      LoginUseCase(getIt<UserRepository>()),
    );

    // post:--------------------------------------------------------------------
    getIt.registerSingleton<GetPostUseCase>(
      GetPostUseCase(getIt<PostRepository>()),
    );
    getIt.registerSingleton<FindPostByIdUseCase>(
      FindPostByIdUseCase(getIt<PostRepository>()),
    );
    getIt.registerSingleton<InsertPostUseCase>(
      InsertPostUseCase(getIt<PostRepository>()),
    );
    getIt.registerSingleton<UpdatePostUseCase>(
      UpdatePostUseCase(getIt<PostRepository>()),
    );
    getIt.registerSingleton<DeletePostUseCase>(
      DeletePostUseCase(getIt<PostRepository>()),
    );

    // question:----------------------------------------------------------------
    getIt.registerSingleton<GetQuestionsUseCase>(
      GetQuestionsUseCase(getIt<QuestionRepository>()),
    );
    /// topic:--------------------------------------------------------------------
    getIt.registerSingleton<GetTopicsUsecase>(GetTopicsUsecase(getIt<TopicRepository>()));
    getIt.registerSingleton<FindTopicByIdUsecase>(FindTopicByIdUsecase(getIt<TopicRepository>()));


    //lecture:--------------------------------------------------------------------
    getIt.registerSingleton<GetLecturesUsecase>(GetLecturesUsecase(getIt<LectureRepository>()));
    getIt.registerSingleton<GetLecturesAreLearningUsecase>(GetLecturesAreLearningUsecase(getIt<LectureRepository>()));
    ///exercise:--------------------------------------------------------------------
    getIt.registerSingleton<GetExercisesUseCase>(GetExercisesUseCase(getIt<ExerciseRepository>()));

    //search:--------------------------------------------------------------------
    getIt.registerSingleton<GetHistorySearchList>(GetHistorySearchList(getIt<SearchRepository>()));
    getIt.registerSingleton<GetSearchLecturesResult>(GetSearchLecturesResult(getIt<SearchRepository>()));
  }
}