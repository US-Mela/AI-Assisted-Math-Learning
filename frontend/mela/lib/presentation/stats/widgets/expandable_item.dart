import 'package:flutter/material.dart';

import '../store/stats_store.dart';

import 'bar_chart.dart';

import '../../../constants/assets.dart';
import '../../../constants/app_theme.dart';


class ExpandableItem extends StatefulWidget {
  final Item item;
  final StatisticsStore store;
  final int index;

  ExpandableItem({super.key, required this.item, required this.store, required this.index});

  @override
  _ExpandableItemState createState() => _ExpandableItemState();
}

class _ExpandableItemState extends State<ExpandableItem> {
  bool _isExpanded = false;

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: EdgeInsets.symmetric(vertical: 5, horizontal: 16),
      color: !_isExpanded ? Colors.white : Theme.of(context).colorScheme.appBackground,
      child: Column(
        children: [
          GestureDetector(
            onTap: () {
              setState(() {
                _isExpanded = !_isExpanded;
              });
            },
            child: Padding(
              padding: const EdgeInsets.symmetric(vertical: 8.0, horizontal: 16.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Text(
                        '${widget.item.title} ${widget.item.currentProgress}/${widget.item.total}',
                        style: Theme.of(context).textTheme.normal
                            .copyWith(color: Colors.black),
                      ),
                      Image.asset(
                        _isExpanded ? 'assets/icons/stats_hide.png' : 'assets/icons/stats_show.png',
                        width: 18,
                        height: 18,
                      )
                    ],
                  ),
                  const SizedBox(height: 3),
                  Padding(
                    padding: const EdgeInsets.only(left: 25.0, right: 25.0, bottom: 1.0),
                    child: LinearProgressIndicator(
                      minHeight: 12,
                      value: widget.item.currentProgress * 1.0 / widget.item.total,
                      color: Theme.of(context).colorScheme.buttonYesBgOrText,
                      backgroundColor: Theme.of(context).colorScheme.textInBg1.withOpacity(0.2),
                      borderRadius: BorderRadius.circular(10.0),
                    ),
                  ),
                ],
              ),
            ),
          ),
          if (_isExpanded)
            Padding(
              padding: EdgeInsets.only(left: 16.0, right: 16.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  SizedBox(height: 8),
                  Text(
                    'Bài tập đã hoàn thành trong chuyên đề',
                    style: Theme.of(context).textTheme.subHeading
                        .copyWith(color: Theme.of(context).colorScheme.textInBg1),
                  ),
                  SizedBox(height: 8),
                  BarChartWidget(),
                  SizedBox(height: 8),
                ],
              ),
            ),
        ],
      ),
    );
  }
}