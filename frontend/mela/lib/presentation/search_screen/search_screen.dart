import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_mobx/flutter_mobx.dart';
import 'package:mela/constants/app_theme.dart';
import 'package:mela/core/widgets/progress_indicator_widget.dart';
import 'package:mela/di/service_locator.dart';
import 'package:mela/presentation/filter_screen/store/filter_store.dart';
import 'package:mela/presentation/lectures_in_topic_screen/widgets/lecture_item.dart';
import 'package:mela/presentation/search_screen/widgets/search_bar.dart';

import '../../constants/route_observer.dart';
import 'store/search_store.dart';

class SearchScreen extends StatefulWidget {
  const SearchScreen({super.key});

  @override
  State<SearchScreen> createState() => _SearchScreenState();
}

class _SearchScreenState extends State<SearchScreen> {
  SearchStore _searchStore = getIt<SearchStore>();
  FilterStore _filterStore = getIt<FilterStore>();
  final GlobalKey<SearchingBarState> searchBarKey =
      GlobalKey<SearchingBarState>();
  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    //first time running
    if (!_searchStore.isLoadingHistorySearch) {
      _searchStore.getHistorySearchList();
    }
  }


  void handleHistoryItemClick(String searchText) {
    // Update the search bar text
    // print("searchBarKey.currentState is not null");
    searchBarKey.currentState?.controller.text = searchText;
    // Perform the search
    searchBarKey.currentState?.performSearch(searchText);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("Tìm kiếm",
            style: Theme.of(context)
                .textTheme
                .heading
                .copyWith(color: Theme.of(context).colorScheme.primary)),
        leading: IconButton(
          onPressed: () async {
            if (_searchStore.isSearched) {
              _searchStore.resetIsSearched();
              _filterStore.resetFilter();
              _searchStore.setIsFiltered(false);
              _searchStore.resetErrorString();
              await _searchStore.getHistorySearchList();
              return;
            }
            //if not issearched, pop the screen
            Navigator.of(context).pop();
            _searchStore.resetIsSearched();
            _searchStore.setIsFiltered(false);
            _filterStore.resetFilter();
            _searchStore.resetErrorString();
          },
          icon: const Icon(Icons.arrow_back),
        ),
      ),
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          //Search bar
          SearchingBar(key: searchBarKey),

          //Row History search or number of lectures after searching
          Observer(builder: (context) {
            if (!_searchStore.isSearched) {
              return Padding(
                padding: const EdgeInsets.only(left: 30),
                child: Text("Lịch sử tìm kiếm",
                    style: Theme.of(context).textTheme.normal.copyWith(
                        color: Theme.of(context).colorScheme.primary)),
              );
            }
            if (_searchStore.isLoadingSearch) {
              return Container();
            }
            if (_searchStore.errorString.isEmpty) {
              return Padding(
                padding: const EdgeInsets.symmetric(horizontal: 20),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text("Tìm thấy",
                        style: Theme.of(context).textTheme.normal.copyWith(
                            color: Theme.of(context).colorScheme.primary)),
                    Text(
                        "${_searchStore.lecturesAfterSearchingAndFiltering!.lectures.length.toString()} kết quả",
                        style: Theme.of(context).textTheme.normal.copyWith(
                            color: Theme.of(context).colorScheme.tertiary)),
                  ],
                ),
              );
            }
            //if error
            return Container();
          }),

          //List history search or list lectures after searching
          Expanded(
            child: Observer(builder: (context) {
              //List history search
              if (!_searchStore.isSearched) {
                return _searchStore.isLoadingHistorySearch
                    ? AbsorbPointer(
                        absorbing: true,
                        child: Stack(
                          alignment: Alignment.center,
                          children: [
                            Container(
                              color: Theme.of(context)
                                  .colorScheme
                                  .surface
                                  .withOpacity(0.2),
                            ),
                            const CustomProgressIndicatorWidget(),
                          ],
                        ),
                      )
                    : ListView.builder(
                        itemCount: _searchStore.searchHistory!.length,
                        itemBuilder: (context, index) {
                          return Container(
                              height: 40,
                              padding:
                                  const EdgeInsets.symmetric(horizontal: 12),
                              margin: const EdgeInsets.symmetric(
                                  vertical: 2, horizontal: 20),
                              child: Row(
                                children: [
                                  Expanded(
                                    child: GestureDetector(
                                      onTap: () {
                                        handleHistoryItemClick(
                                            _searchStore.searchHistory![index]);
                                      },
                                      child: Text(
                                        _searchStore.searchHistory![index],
                                        style: TextStyle(fontSize: 12),
                                        softWrap: true,
                                        maxLines: null,
                                        overflow: TextOverflow.visible,
                                      ),
                                    ),
                                  ),
                                  InkWell(
                                    onTap: () {},
                                    child: const Icon(
                                      Icons.close,
                                      size: 14,
                                    ),
                                  ),
                                ],
                              ));
                        },
                      );
              }

              if (_searchStore.isLoadingSearch) {
                return const Center(child: CustomProgressIndicatorWidget());
              }
              if (_searchStore.errorString.isNotEmpty) {
                return Center(
                  child: Text(
                    _searchStore.errorString,
                    style: const TextStyle(color: Colors.red),
                  ),
                );
              }

              ///List lectures after searching
              return _searchStore.isLoadingSearch
                  ? const Center(child: CustomProgressIndicatorWidget())
                  : ListView.builder(
                      itemCount: _searchStore
                          .lecturesAfterSearchingAndFiltering!.lectures.length,
                      itemBuilder: (context, index) {
                        return LectureItem(
                            lecture: _searchStore
                                .lecturesAfterSearchingAndFiltering!
                                .lectures[index]);
                      },
                    );
            }),
          ),
        ],
      ),
    );
  }
}