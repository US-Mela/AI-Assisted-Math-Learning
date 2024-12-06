import 'dart:async';

import 'package:mela/domain/repository/lecture/lecture_repository.dart';
import 'package:mela/domain/repository/post/post_repository.dart';
import 'package:mela/domain/repository/user/user_repository.dart';
import 'package:mela/domain/repository/user_register/user_signup_repostiory.dart';

import 'package:mela/domain/usecase/exercise/get_exercises_usecase.dart';
import 'package:mela/domain/usecase/lecture/get_divided_lecture_usecase.dart';
import 'package:mela/domain/usecase/lecture/get_lectures_usecase.dart';
import 'package:mela/domain/usecase/lecture/get_levels_usecase.dart';
import 'package:mela/domain/usecase/post/get_post_usecase.dart';
import 'package:mela/domain/usecase/search/get_search_lectures_result.dart';
import 'package:mela/domain/usecase/topic/find_topic_by_id_usecase.dart';
import 'package:mela/domain/usecase/topic/get_topics_usecase.dart';
import 'package:mela/domain/usecase/user/get_user_info_usecase.dart';
import 'package:mela/domain/usecase/user_login/refresh_access_token_usecase.dart';
import 'package:mela/domain/usecase/user_login/save_access_token_usecase.dart';
import 'package:mela/domain/usecase/user_login/save_refresh_token_usecase.dart';

import '../../../di/service_locator.dart';

import '../../repository/exercise/exercise_repository.dart';
import '../../repository/question/question_repository.dart';
import '../../repository/search/search_repository.dart';
import '../../repository/topic/topic_repository.dart';
import '../../repository/user_login/user_login_repository.dart';

import '../../usecase/lecture/get_lectures_are_learning_usecase.dart';
import '../../usecase/question/get_questions_usecase.dart';
import '../../usecase/search/get_history_search_list.dart';
import '../../usecase/user_login/is_logged_in_usecase.dart';
import '../../usecase/user_login/login_usecase.dart';
import '../../usecase/user_login/save_login_in_status_usecase.dart';
import '../../usecase/user_signup/signup_usecase.dart';

import '../../repository/stat/stat_repository.dart';
import 'package:mela/domain/usecase/stat/get_progress_usecase.dart';
import 'package:mela/domain/usecase/stat/get_detailed_progress_usecase.dart';

class UseCaseModule {
  static Future<void> configureUseCaseModuleInjection() async {
    //post:---------------------------------------------------------------------
    getIt.registerSingleton<GetPostUseCase>(
        GetPostUseCase(getIt<PostRepository>()));

    // user:--------------------------------------------------------------------
    getIt.registerSingleton<GetUserInfoUseCase>(
        GetUserInfoUseCase(getIt<UserRepository>()));
    // user login:--------------------------------------------------------------
    getIt.registerSingleton<IsLoggedInUseCase>(
      IsLoggedInUseCase(getIt<UserLoginRepository>()),
    );
    getIt.registerSingleton<SaveLoginStatusUseCase>(
      SaveLoginStatusUseCase(getIt<UserLoginRepository>()),
    );
    getIt.registerSingleton<LoginUseCase>(
      LoginUseCase(getIt<UserLoginRepository>()),
    );

    getIt.registerSingleton<SaveAccessTokenUsecase>(
        SaveAccessTokenUsecase(getIt<UserLoginRepository>()));

    getIt.registerSingleton<SaveRefreshTokenUsecase>(
        SaveRefreshTokenUsecase(getIt<UserLoginRepository>()));
    getIt.registerSingleton<RefreshAccessTokenUsecase>(
        RefreshAccessTokenUsecase(getIt<UserLoginRepository>()));
    //user signup:--------------------------------------------------------------
    getIt.registerSingleton<SignupUseCase>(
      SignupUseCase(getIt<UserSignUpRepository>()),
    );

    // question:----------------------------------------------------------------
    getIt.registerSingleton<GetQuestionsUseCase>(
      GetQuestionsUseCase(
          getIt<QuestionRepository>(), getIt<RefreshAccessTokenUsecase>()
      ),
    );

    getIt.registerSingleton<SubmitResultUseCase>(SubmitResultUseCase(
          getIt<SaveResultApi>(), getIt<RefreshAccessTokenUsecase>()
      ),
    );
    /// topic:------------------------------------------------------------------
    getIt.registerSingleton<GetTopicsUsecase>(GetTopicsUsecase(
        getIt<TopicRepository>(), getIt<RefreshAccessTokenUsecase>()));

    getIt.registerSingleton<FindTopicByIdUsecase>(
        FindTopicByIdUsecase(getIt<TopicRepository>()));


    //lecture:--------------------------------------------------------------------
    getIt.registerSingleton<GetLecturesUsecase>(GetLecturesUsecase(
        getIt<LectureRepository>(), getIt<RefreshAccessTokenUsecase>()));

    getIt.registerSingleton<GetLecturesAreLearningUsecase>(
        GetLecturesAreLearningUsecase(
            getIt<LectureRepository>(), getIt<RefreshAccessTokenUsecase>()));

    getIt.registerSingleton<GetLevelsUsecase>(GetLevelsUsecase(
        getIt<LectureRepository>(), getIt<RefreshAccessTokenUsecase>()));

    getIt.registerSingleton<GetDividedLectureUsecase>(GetDividedLectureUsecase(
        getIt<LectureRepository>(), getIt<RefreshAccessTokenUsecase>()));

    ///exercise:--------------------------------------------------------------------
    getIt.registerSingleton<GetExercisesUseCase>(GetExercisesUseCase(
        getIt<ExerciseRepository>(), getIt<RefreshAccessTokenUsecase>()));

    //search:--------------------------------------------------------------------
    getIt.registerSingleton<GetHistorySearchListUsecase>(
        GetHistorySearchListUsecase(getIt<SearchRepository>()));
    getIt.registerSingleton<GetSearchLecturesResultUsecase>(
        GetSearchLecturesResultUsecase(
            getIt<SearchRepository>(), getIt<RefreshAccessTokenUsecase>()));

    getIt.registerSingleton<AddHistorySearchUsecase>(
        AddHistorySearchUsecase(getIt<SearchRepository>()));

    getIt.registerSingleton<DeleteHistorySearchUsecase>(
        DeleteHistorySearchUsecase(getIt<SearchRepository>()));

    getIt.registerSingleton<DeleteAllHistorySearchUsecase>(
        DeleteAllHistorySearchUsecase(getIt<SearchRepository>()));

    //stats:--------------------------------------------------------------------
    getIt.registerSingleton<GetProgressListUseCase>(
      GetProgressListUseCase(getIt<StatRepository>(), getIt<RefreshAccessTokenUsecase>()),
    );
    getIt.registerSingleton<GetDetailedProgressListUseCase>(
      GetDetailedProgressListUseCase(getIt<StatRepository>()),
    );
    //stat search---------------------------------------------------------------
    getIt.registerSingleton<GetStatSearchHistoryUseCase>(
      GetStatSearchHistoryUseCase(getIt<StatSearchRepository>()),
    );
    getIt.registerSingleton<UpdateStatSearchHistoryUseCase>(
      UpdateStatSearchHistoryUseCase(getIt<StatSearchRepository>()),
    );
    // user:--------------------------------------------------------------------
    getIt.registerSingleton<GetUserInfoUseCase>(
        GetUserInfoUseCase(getIt<UserRepository>(), getIt<RefreshAccessTokenUsecase>()));

    getIt.registerSingleton<LogoutUseCase>(
        LogoutUseCase(getIt<UserRepository>())
    );

    //forgot password:----------------------------------------------------------
    getIt.registerSingleton<VerifyExistEmailUseCase>(
        VerifyExistEmailUseCase(getIt<ForgotPasswordRepository>()));
    getIt.registerSingleton<VerifyOTPUseCase>(
        VerifyOTPUseCase(getIt<ForgotPasswordRepository>()));
    getIt.registerSingleton<CreateNewPasswordUsecase>(
        CreateNewPasswordUsecase(getIt<ForgotPasswordRepository>()));
  }

  // I brought register for user info and logout to the back as RefreshAccessTokenUsecase HAS to be registered first
  // toi dem register user ra sau boi vi RefreshAccessTokenUsecase phai duoc dang ky truoc khi dang ky getUserInfo

}

