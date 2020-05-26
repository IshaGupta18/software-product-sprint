// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class FindMeetingQuery {
  public ArrayList<TimeRange> mergeIntervals(ArrayList<TimeRange> Intervals){

    // Sort the intevals of events by their sort time so that the overlapping intervals can be merged

    Intervals.sort(TimeRange.ORDER_BY_START);

    ArrayList<TimeRange> mergedTimeRanges = new ArrayList<TimeRange>();
    mergedTimeRanges.add(Intervals.get(0));
    int mergedTimeRangesCounter=0;

    for (int i=1;i<Intervals.size();i++){

      TimeRange blockedTimeRange = Intervals.get(i);
      TimeRange lastTimeRange = mergedTimeRanges.get(mergedTimeRangesCounter);

      // If a time range contains or is equal to the next time range, simply skip the next time range

      if (lastTimeRange.equals(blockedTimeRange) || lastTimeRange.contains(blockedTimeRange)){
        continue;
      }

      // If two time ranges overlap, remove the previous time range and replace it with the combined interval of the two overlapping time ranges

      else if (lastTimeRange.overlaps(blockedTimeRange)){
        TimeRange timeRange = TimeRange.fromStartEnd(lastTimeRange.start(), blockedTimeRange.end(),false);
        mergedTimeRanges.remove(mergedTimeRangesCounter);
        mergedTimeRanges.add(timeRange);
      }

      //If the two time ranges are disjoint, simply add the current time range to collection
      
      else{
        mergedTimeRanges.add(blockedTimeRange);
        mergedTimeRangesCounter++;
      }

    }
    return mergedTimeRanges;
  }
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {

    // Remove all attendees from an event who are not in the meeting request
    // This way, if an event has no attendees important to us, that interval of time is freed up for scheduling the meeting

    Iterator eventsIterator = events.iterator();
    ArrayList<TimeRange> blockedTimeRanges = new ArrayList<TimeRange>();

    while (eventsIterator.hasNext()){
      
      Event event = (Event) eventsIterator.next();
      Set<String> eventAttendees = event.getAttendees();
      Iterator allAttendeesIterator = request.getAttendees().iterator();
      Set<String> newEventAttendees = new HashSet<String>();
      
      while (allAttendeesIterator.hasNext()){
        String person = (String) allAttendeesIterator.next();
        
        // If an event contains an attendee from meeting request, add it to the new attendee list for that event 

        if (eventAttendees.contains(person)){
          newEventAttendees.add(person);
        }
      }

      // If the attendee list of the Event is not empty ie atleast one person from the meeting list attends this event, then block this time range as its unavailable to hold a meeting

      if (!newEventAttendees.isEmpty()){
        blockedTimeRanges.add(event.getWhen());
      }
    }

    ArrayList<TimeRange> availableTimeRanges = new ArrayList<TimeRange>();

    //Case 1: All attendees attend no event ie the entire day is available for scheduling the meeting

    if (blockedTimeRanges.isEmpty()){

      if (request.getDuration()<=TimeRange.END_OF_DAY){
        availableTimeRanges.add(TimeRange.WHOLE_DAY);
      }

    }

    //Case 2: blockedTimeRanges contains the Time Ranges of the events which is attended by atleast one of the attendee in the meeting request list of attendees.

    else{

      ArrayList<TimeRange> mergedTimeRanges = mergeIntervals(blockedTimeRanges);
      
      int currentStart = TimeRange.START_OF_DAY;

      for (TimeRange interval: mergedTimeRanges){

        // If the duration from the end of one interval to the start of next is greater than or equal to the required duration of time, add the interval to the available time range

        if ((interval.start()-currentStart)>=request.getDuration()){
          availableTimeRanges.add(TimeRange.fromStartEnd(currentStart, interval.start(), false));
        }
        currentStart = interval.end();
      }

      // Check if the last interval and the End of Day have enough duration for an available time range

      if ((TimeRange.END_OF_DAY-currentStart)>=request.getDuration()){
        availableTimeRanges.add(TimeRange.fromStartEnd(currentStart, TimeRange.END_OF_DAY, true));
      }
    }

    return availableTimeRanges;
  }
}
