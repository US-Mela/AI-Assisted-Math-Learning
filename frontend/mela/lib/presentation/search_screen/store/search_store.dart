import 'package:mela/domain/entity/lecture/lecture_list.dart';
import 'package:mela/domain/usecase/search/get_history_search_list.dart';
import 'package:mela/domain/usecase/search/get_search_lectures_result.dart';
import 'package:mobx/mobx.dart';

import '../../../../domain/usecase/topic/get_topics_usecase.dart';
// Include generated file
part 'search_store.g.dart';

// This is the class used by rest of your codebase
class SearchStore = _SearchStore with _$SearchStore;

abstract class _SearchStore with Store {
  //UseCase
  GetHistorySearchList _getHistorySearchList;
  GetSearchLecturesResult _getSearchLecturesResult;
  //Constructor
  _SearchStore(this._getHistorySearchList, this._getSearchLecturesResult);

  @observable
  bool isSearched = false; //check press enter to search or not

  @observable
  List<String>? searchHistory;

  @observable
  LectureList? lecturesAfterSearching;

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
  ObservableFuture<List<String>?> fetchHistorySearchFuture =
      ObservableFuture<List<String>?>(ObservableFuture.value(null));

  @action
  Future getHistorySearchList() async {
    final future = _getHistorySearchList.call(params: null);
    fetchHistorySearchFuture = ObservableFuture(future);
    future.then((value) {
      this.searchHistory = value;
    }).catchError((onError) {
      this.searchHistory = null;
      print(onError);
      throw onError;
    });
  }

  @action
  Future getLecturesAfterSearch(String txtSearching) async {
    final future = _getSearchLecturesResult.call(params: txtSearching);
    fetchLecturesAfterSearchingFuture = ObservableFuture(future);

    future.then((value) {
      this.lecturesAfterSearching = value;
    }).catchError((onError) {
      this.lecturesAfterSearching = null;
      print(onError);
      throw onError;
    });
  }

  @action
  void toggleIsSearched() {
    isSearched = !isSearched;
  }
  @action
  void resetIsSearched() {
    isSearched = false;
  }
}
