import 'package:dio/dio.dart';
import 'package:mela/domain/entity/history_search/history_search.dart';
import 'package:mela/domain/entity/lecture/lecture_list.dart';
import 'package:mela/domain/usecase/search/get_history_search_list_usecase.dart';
import 'package:mela/domain/usecase/search/get_search_lectures_result_usecase.dart';
import 'package:mela/utils/dio/dio_error_util.dart';
import 'package:mobx/mobx.dart';

// Include generated file
part 'search_store.g.dart';

// This is the class used by rest of your codebase
class SearchStore = _SearchStore with _$SearchStore;

abstract class _SearchStore with Store {
  //UseCase
  GetHistorySearchListUsecase _getHistorySearchListUsecase;
  GetSearchLecturesResultUsecase _getSearchLecturesResultUsecase;

  //Constructor
  _SearchStore(this._getHistorySearchListUsecase, this._getSearchLecturesResultUsecase);

  @observable
  bool isSearched = false; //check press enter to search or not

  @observable
  bool isFiltered = false;

  @observable
  bool isUnAuthorized = false;

  @observable
  String errorString = '';

  @observable
  List<HistorySearch>? searchHistory;

  @observable
  LectureList? lecturesAfterSearchingAndFiltering;

  @observable
  LectureList?
      lecturesAfterSearching; // it is not used because it is same with lecture.lecureList

  @computed
  bool get isLoadingSearch =>
      fetchLecturesAfterSearchingFuture.status == FutureStatus.pending;
  @computed
  bool get isLoadingHistorySearch =>
      fetchHistorySearchFuture.status == FutureStatus.pending;

  @observable
  ObservableFuture<LectureList?> fetchLecturesAfterSearchingFuture =
      ObservableFuture<LectureList?>(ObservableFuture.value(null));

  @observable
  ObservableFuture<List<HistorySearch>?> fetchHistorySearchFuture =
      ObservableFuture<List<HistorySearch>?>(ObservableFuture.value(null));

  @action
  Future getHistorySearchList() async {
    final future = _getHistorySearchListUsecase.call(params: null);
    fetchHistorySearchFuture = ObservableFuture(future);
    future.then((value) {
      this.searchHistory = value;
    }).catchError((onError) {
      this.searchHistory = null;
      //errorString = onError.toString();
    });
  }

  @action
  Future getLecturesAfterSearch(String txtSearching) async {
    final future = _getSearchLecturesResultUsecase.call(params: txtSearching);
    fetchLecturesAfterSearchingFuture = ObservableFuture(future);

    try {
      final value = await future;
      lecturesAfterSearching = value;
      updateLectureAfterSeachingAndFiltering(value);
    } catch (onError) {
      lecturesAfterSearching = null;
      updateLectureAfterSeachingAndFiltering(null);

      if (onError is DioException) {
        if (onError.response?.statusCode == 401) {
          isUnAuthorized = true;
          return;
        }
        errorString = DioExceptionUtil.handleError(onError);
      } else {
        errorString = "Có lỗi, thử lại sau";
      }
    }
  }

  @action
  void updateLectureAfterSeachingAndFiltering(LectureList? value) {
    lecturesAfterSearchingAndFiltering = value;
  }

  @action
  void toggleIsSearched() {
    isSearched = !isSearched;
  }

  @action
  void resetIsSearched() {
    isSearched = false;
  }

  @action
  void setIsFiltered(bool value) {
    isFiltered = value;
  }

  @action
  void resetErrorString() {
    errorString = '';
  }
}
