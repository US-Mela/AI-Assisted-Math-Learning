import 'package:flutter/material.dart';
import 'package:mela/domain/entity/stat/progress.dart';

import '../../../constants/assets.dart';
import '../../../constants/app_theme.dart';
import '../../../domain/entity/stat/score_record.dart';
import 'line_chart_widget.dart';


class ExpandableItem extends StatefulWidget {
  final Progress item;

  ExpandableItem({super.key, required this.item});

  @override
  _ExpandableItemState createState() => _ExpandableItemState();
}

class _ExpandableItemState extends State<ExpandableItem> {
  bool _isExpanded = false;
  late String type;
  late ProgressExercise? prog_ex;
  late ProgressSection? prog_sect;
  late List<ScoreRecord> scores;
  
  @override
  Widget build(BuildContext context) {
    type = widget.item.type;
    prog_ex = widget.item.exercise;
    prog_sect = widget.item.section;
    scores = prog_ex?.scoreRecords ?? [];
    return Card(
      margin: const EdgeInsets.symmetric(vertical: 5, horizontal: 16),
      elevation: 5.0,
      color: !_isExpanded ? Colors.white : Theme.of(context).colorScheme.appBackground,
      child: Column(
        children: [
          GestureDetector(
            onTap: () {
              if (type != 'SECTION' && scores.length <= 1) {
                setState(() {
                  _isExpanded = !_isExpanded;
                });
              }
            },
            child: Padding(
              padding: const EdgeInsets.symmetric(vertical: 8.0, horizontal: 16.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      ConstrainedBox(
                        constraints: const BoxConstraints(
                          maxWidth: 150,
                          minWidth: 150,
                        ),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text( //Tên bài tập hoặc section
                              (type == 'SECTION') 
                                  ? prog_sect?.sectionName ?? "" 
                                  : prog_ex?.exerciseName ?? "",
                              style: Theme.of(context).textTheme.title
                                  .copyWith(color: Theme.of(context).colorScheme.onPrimary),
                            ),
                            Text( //Tên bài học
                              widget.item.lectureName ?? "",
                              style: Theme.of(context).textTheme.normal
                                  .copyWith(color: Theme.of(context).colorScheme.textInBg1),
                            ),
                            Text( //Tên chủ đề
                              widget.item.topicName ?? "",
                              style: Theme.of(context).textTheme.normal
                                  .copyWith(color: Theme.of(context).colorScheme.textInBg2),
                            ),
                          ],
                        ),
                      ),
                      Column(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          children: [
                            Text(//học bài hay làm bài?
                              (type == 'SECTION') ? "Đã học" : "Đã làm bài",
                              style: Theme.of(context).textTheme.normal
                                  .copyWith(color: Theme.of(context).colorScheme.textInBg1),
                            ),
                            Text(//date
                              widget.item.latestDate,
                              style: Theme.of(context).textTheme.normal
                                  .copyWith(color: Theme.of(context).colorScheme.textInBg1),
                            ),
                            if (type != 'SECTION')
                              Text(//score
                                "${(widget.item.exercise?.latestScore ?? 0) as String} Điểm",
                                style: Theme.of(context).textTheme.bigTitle
                                    .copyWith(color: Theme.of(context).colorScheme.onPrimary),
                              ),
                          ]
                      ),
                      Row(
                          mainAxisAlignment: MainAxisAlignment.end,
                          children: [
                            Image.asset(
                              Assets.stats_gain,
                              width: 21,
                              height: 21,
                            ),
                            const SizedBox(width: 5),
                            if (type != 'SECTION' && scores.length <= 1)
                              Image.asset(
                                _isExpanded ? Assets.stats_hide : Assets.stats_show,
                                width: 16,
                                height: 16,
                              )
                            else const SizedBox(width: 16, height: 16),
                          ]
                      ),
                    ],
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
                  SizedBox(height: 1),
                  LineChartWidget(scores: scores),
                  SizedBox(height: 8),
                ],
              ),
            ),
        ],
      ),
    );
  }
}
