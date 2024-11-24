import 'dart:async';


import 'package:flutter/material.dart';
import 'package:flutter_mobx/flutter_mobx.dart';
import 'package:mela/constants/app_theme.dart';

import 'widgets/expandable_list.dart';

import 'store/stats_store.dart';
import '../../di/service_locator.dart';

class StatisticsScreen extends StatefulWidget {
  StatisticsScreen({super.key});

  @override
  State<StatisticsScreen> createState() => _StatisticsScreenState();
}

class _StatisticsScreenState extends State<StatisticsScreen> {
  //Stores:---------------------------------------------------------------------
  final StatisticsStore _store = getIt<StatisticsStore>();
  //State set:------------------------------------------------------------------

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    await _store.getProgressList();
    await _store.getDetailedProgressList();
  }

  @override
  void didChangeDependencies() {
    // TODO: implement didChangeDependencies
    super.didChangeDependencies();
  }

  @override
  void dispose() {
    // TODO: implement dispose
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
      length: 3,
      child: Scaffold(
        appBar: AppBar(
          automaticallyImplyLeading: false,
          backgroundColor: Theme.of(context).colorScheme.appBackground,
          elevation: 0,
          title: Text(
            'Thống kê',
            style: Theme.of(context).textTheme.heading.copyWith(
              color: Theme.of(context).colorScheme.textInBg1,
            ),
          ),
          actions: [
            Padding(
              padding: const EdgeInsets.only(right: 10),
              child: IconButton(
                onPressed: () {},
                icon: const Icon(Icons.search),
                color: Theme.of(context).colorScheme.appBackground,
              ),
            )
          ],
          bottom: TabBar(
            labelColor: Theme.of(context).colorScheme.buttonYesBgOrText,
            unselectedLabelColor: Theme.of(context).colorScheme.textInBg1,
            indicatorColor: Theme.of(context).colorScheme.buttonYesBgOrText,
            tabs: const [
              Tab(text: "Tiểu học"),
              Tab(text: "Trung học"),
              Tab(text: "Phổ thông"),
            ],
          ),
        ),
        body: Observer(
          builder: (context) {
            if (_store.progressLoading || _store.detailedProgressLoading) {
              return Center(child: CircularProgressIndicator());
            }
            return TabBarView(
              children: [
                ExpandableList(store: _store, division: "Tiểu học"),
                ExpandableList(store: _store, division: "Trung học"),
                ExpandableList(store: _store, division: "Phổ thông"),
              ],
            );
          },
        ),
        backgroundColor: Theme.of(context).colorScheme.appBackground,
      ),
    );
  }
}


