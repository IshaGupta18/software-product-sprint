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

    return availableTimeRanges;
  }
}
