// import 'package:flutter/foundation.dart';
// import 'package:flutter/material.dart';
// import 'package:flutter_mobx/flutter_mobx.dart';
// import 'package:mela/constants/app_theme.dart';
// import 'package:mela/core/widgets/progress_indicator_widget.dart';
// import 'package:mela/di/service_locator.dart';
// import 'package:mela/presentation/stats/widgets/expandable_list.dart';
//
// import 'widgets/search_bar.dart';
// import 'package:mela/presentation/stats/widgets/search_bar.dart';
//
// import 'store/stat_search_store.dart';
// import 'store/stat_filter_store.dart';
//
// class StatSearchScreen extends StatefulWidget {
//   const StatSearchScreen({super.key});
//
//   @override
//   State<StatSearchScreen> createState() => _StatSearchScreenState();
// }
//
// class _StatSearchScreenState extends State<StatSearchScreen> {
//   //
//   StatSearchStore _searchStore = getIt<StatSearchStore>();
//   StatFilterStore _filterStore = getIt<StatFilterStore>();
//   //
//   final GlobalKey<SearchingBarState> searchBarKey =
//   GlobalKey<SearchingBarState>();
//
//   @override
//   void initState() {
//     // TODO: implement initState
//     super.initState();
//     _loadData();
//   }
//
//   Future<void> _loadData() async {
//     await _searchStore.getStatSearchHistory();
//   }
//
//   @override
//   void didChangeDependencies() {
//     super.didChangeDependencies();
//   }
//
//
//   void handleHistoryItemClick(String searchText) {
//     // Update the search bar text
//     // print("searchBarKey.currentState is not null");
//     searchBarKey.currentState?.controller.text = searchText;
//     // Perform the search
//     searchBarKey.currentState?.performSearch(searchText);
//   }
//
//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//       appBar: AppBar(
//         title: Text("Tìm kiếm",
//             style: Theme.of(context)
//                 .textTheme
//                 .heading
//                 .copyWith(color: Theme.of(context).colorScheme.primary)),
//         leading: IconButton(
//           onPressed: () async {
//             if (_searchStore.isSearched) {
//               _searchStore.resetIsSearched();
//               _filterStore.resetFilter();
//               _searchStore.setIsFiltered(false);
//               _searchStore.resetErrorString();
//               await _searchStore.getStatSearchHistory();
//               return;
//             }
//             //if not issearched, pop the screen
//             Navigator.of(context).pop();
//             _searchStore.resetIsSearched();
//             _searchStore.setIsFiltered(false);
//             _filterStore.resetFilter();
//             _searchStore.resetErrorString();
//           },
//           icon: const Icon(Icons.arrow_back),
//         ),
//       ),
//       body: Column(
//         crossAxisAlignment: CrossAxisAlignment.start,
//         children: [
//           //Search bar
//           SearchingBar(key: searchBarKey),
//           //Row History search or number of lectures after searching
//           Observer(builder: (context) {
//             if (!_searchStore.isSearched) {
//               return Padding(
//                 padding: const EdgeInsets.only(left: 30),
//                 child: Text("Lịch sử tìm kiếm",
//                     style: Theme.of(context).textTheme.normal.copyWith(
//                         color: Theme.of(context).colorScheme.primary)),
//               );
//             }
//             if (_searchStore.isLoadingSearch) {
//               return Container();
//             }
//             if (_searchStore.errorString.isEmpty) {
//               return Padding(
//                 padding: const EdgeInsets.symmetric(horizontal: 20),
//                 child: Row(
//                   mainAxisAlignment: MainAxisAlignment.spaceBetween,
//                   children: [
//                     Text("Tìm thấy",
//                         style: Theme.of(context).textTheme.normal.copyWith(
//                             color: Theme.of(context).colorScheme.primary)),
//                     Text(
//                         "${_searchStore.listAfterSearchingAndFiltering!.progressList?.length.toString()} kết quả",
//                         style: Theme.of(context).textTheme.normal.copyWith(
//                             color: Theme.of(context).colorScheme.tertiary)),
//                   ],
//                 ),
//               );
//             }
//             //if error
//             return Container();
//           }),
//
//           //List history search or list after searching
//           Expanded(
//             child: Observer(builder: (context) {
//               //List history search
//               if (!_searchStore.isSearched) {
//                 return _searchStore.isLoadingHistorySearch
//                     ? AbsorbPointer(
//                   absorbing: true,
//                   child: Stack(
//                     alignment: Alignment.center,
//                     children: [
//                       Container(
//                         color: Theme
//                             .of(context)
//                             .colorScheme
//                             .surface
//                             .withOpacity(0.2),
//                       ),
//                       const CustomProgressIndicatorWidget(),
//                     ],
//                   ),
//                 )
//                     : ListView.builder(
//                   itemCount: _searchStore.searchHistory!.length,
//                   itemBuilder: (context, index) {
//                     return Container(
//                         height: 40,
//                         padding:
//                         const EdgeInsets.symmetric(horizontal: 12),
//                         margin: const EdgeInsets.symmetric(
//                             vertical: 2, horizontal: 20),
//                         child: Row(
//                           children: [
//                             Expanded(
//                               child: GestureDetector(
//                                 onTap: () {
//                                   handleHistoryItemClick(
//                                       _searchStore.searchHistory![index]);
//                                 },
//                                 child: Text(
//                                   _searchStore.searchHistory![index],
//                                   style: TextStyle(fontSize: 12),
//                                   softWrap: true,
//                                   maxLines: null,
//                                   overflow: TextOverflow.visible,
//                                 ),
//                               ),
//                             ),
//                             InkWell(
//                               onTap: () {},
//                               child: const Icon(
//                                 Icons.close,
//                                 size: 14,
//                               ),
//                             ),
//                           ],
//                         ));
//                   },
//                 );
//               }
//
//               if (_searchStore.isLoadingSearch) {
//                 return const Center(child: CustomProgressIndicatorWidget());
//               }
//               if (_searchStore.errorString.isNotEmpty) {
//                 return Center(
//                   child: Text(
//                     _searchStore.errorString,
//                     style: const TextStyle(color: Colors.red),
//                   ),
//                 );
//               }
//
//               ///List after searching
//               return ExpandableList(
//                   list: _searchStore.listAfterSearchingAndFiltering
//                       ?.progressList);
//             }),
//           ),
//         ],
//       ),
//     );
//   }
// }