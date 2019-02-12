package edu.studyup.serviceImpl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import edu.studyup.entity.Event;
import edu.studyup.entity.Location;
import edu.studyup.entity.Student;
import edu.studyup.util.DataStorage;
import edu.studyup.util.StudyUpException;
//Partner Name: Christie Frush, Partner Email: cjfrush@ucdavis.edu
class EventServiceImplTest {

	EventServiceImpl eventServiceImpl;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		eventServiceImpl = new EventServiceImpl();
		//Create Student
		Student student = new Student();
		student.setFirstName("John");
		student.setLastName("Doe");
		student.setEmail("JohnDoe@email.com");
		student.setId(1);
		
		//Create Event1
		Event event = new Event();
		event.setEventID(1);
		event.setDate(new Date());
		event.setName("Event 1");
		Location location = new Location(-122, 37);
		event.setLocation(location);
		List<Student> eventStudents = new ArrayList<>();
		eventStudents.add(student);
		event.setStudents(eventStudents);
		
		//Create Event2 -> Event in Future
		Event event2 = new Event();
		event2.setEventID(2);
		Date future = new Date(12121212121212L);
		event2.setDate(future);
		event2.setName("Event 2");
		
		//Event 3 is an example of an invalid date
		
		
		//This Event is an example of a past event
		Event event4 = new Event();
		event4.setEventID(4);
		Date past = new Date(20);
		event4.setDate(past);
		event4.setName("Event 4");
		
		DataStorage.eventData.put(event.getEventID(), event);
		DataStorage.eventData.put(event2.getEventID(), event2);
		DataStorage.eventData.put(event4.getEventID(), event4);

	}

	@AfterEach
	void tearDown() throws Exception {
		DataStorage.eventData.clear();
	}

	@Test
	void testUpdateEventName_GoodCase() throws StudyUpException {
		int eventID = 1;
		eventServiceImpl.updateEventName(eventID, "Renamed Event 1");
		assertEquals("Renamed Event 1", DataStorage.eventData.get(eventID).getName());
	}
	
	@Test
	void testUpdateEvent_WrongEventID_badCase() {
		int eventID = 3;
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.updateEventName(eventID, "Renamed Event 3");
		  });	
	}
////////////////////////   OUR CASES   ////////////////////////////////////////////////
	//Tests if the updateEventName function will throw an error if the Event name is longer
	//than 20 characters
	@Test 
	void testifEventNameLengthErrorIsThrown() {
		//Use Event 1 that was initialized above
		int eventID = 1;
		//Check that an assertion is thrown when we update the name to longer than 20 characters
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.updateEventName(eventID, "ThisNameShouldBeMoreThan20CharactersLongHopefully");
		  });
	}	
	
	//Tests if the function getActiveEvents does return the event that is passed into it
	@Test
	void testifActiveEventReturns_CorrectEvent() throws StudyUpException{
	//Create a new event that will be passed into getActiveEvents
		Event event1 = new Event();
		event1.setEventID(1);
	//Set the date to the present so it will be passed into the getActiveEvents
		Date date = new Date();
		event1.setDate(date);
		event1.setName("Event 1");
		DataStorage.eventData.put(event1.getEventID(), event1);
		List<Event> activeEventsFromFunct = new ArrayList<>();
		activeEventsFromFunct = eventServiceImpl.getActiveEvents();
		assertTrue(activeEventsFromFunct.contains(event1));
	}
	
	//Shows that active events does not return the correct events
	@Test
	void testifPastEvents_GetsCorrectEvent(){
		int eventID = 4;
		List<Event> pastEvents = new ArrayList<>();
		pastEvents = eventServiceImpl.getPastEvents();
		List<Event> pastMockedEvents = new ArrayList<>();
		pastMockedEvents.add(DataStorage.eventData.get(eventID));
		assertEquals(pastEvents, pastMockedEvents);
	}
	/*
	 * Checks to make sure that past events does not contain future events
	 */
	@Test
	void testifPastEvents_GetsIncorrectEvent() {
		int eventID = 2;
		List<Event> pastEvents = new ArrayList<>();
		pastEvents = eventServiceImpl.getPastEvents();
		List<Event> futureMockedEvents = new ArrayList<>();
		futureMockedEvents.add(DataStorage.eventData.get(eventID));
		assertNotEquals(pastEvents, futureMockedEvents);
	}
	/*
	 *  Checks to see if you can add a student to an invalid event
	 */
	@Test
	void checkIfEventAddsStudentToEvent_InvalidEvent(){
		int eventID = 3;
		Student student = new Student();
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.addStudentToEvent(student, eventID);
		  });	
	}
	/*
	 * Checks the clause if there are no present students an empty list is created
	 */
	
	@Test 
	void checkIfNoPresentStudents_ValidEvent() throws StudyUpException{
		Event event = new Event();
		event.setEventID(4);
		event.setDate(new Date());
		event.setName("Event 4");
		Location location = new Location(-112, 67);
		event.setLocation(location);
		List<Student> eventStudents = new ArrayList<>();
		
		eventStudents = event.getStudents();
		
		assertNull(eventStudents);
		assertNotNull(event.getLocation());
	
		Student student = new Student();
		student.setFirstName("Steve");
		student.setLastName("Smith");
		student.setEmail("SteveSmith@email.com");
		student.setId(2);
		
		assertNotNull(eventServiceImpl.addStudentToEvent(student, 4));

	}
	/*
	 * Checks that you can add a valid student to a valid event
	 */
	@Test
	void checkIfEventAddsMultipleStudentsToEvent_ValidEvent() throws StudyUpException{
		int eventID = 1;
		Student student = new Student();
		Event updatedEvent = eventServiceImpl.addStudentToEvent(student, eventID);
		int intialSize = DataStorage.eventData.get(eventID).getStudents().size();
		Student newStudent = new Student();
		updatedEvent = eventServiceImpl.addStudentToEvent(newStudent, eventID);
		int finalSize = DataStorage.eventData.get(eventID).getStudents().size();
		int diff = finalSize - intialSize;
		assertNotEquals(0, diff);
	}
	/*
	 * Check to see if delete actually deletes an event
	 */
	@Test
	void checkDeleteEvent() {
		int eventID = 4;
		eventServiceImpl.deleteEvent(eventID);
		assertNull(DataStorage.eventData.get(eventID));
	}
	
	/* 
	 * Bug found - shows that you cannot have 20 char long names even though
	 * you are supposed to be able to have 20 character long names
	 */
	@Test
	void testifEventNameIsExactly20() throws StudyUpException{
		int eventID = 1;
		eventServiceImpl.updateEventName(eventID, "12345678901234567890");
		assertEquals("12345678901234567890",DataStorage.eventData.get(eventID).getName());
	}
	/* 
	 * Bug found - You can add the same student to a particular event multiple times
	 * which should not be allowed
	 */
	@Test
	void checkIfEventAddsStudentToEvent_SameStudentTwice()throws StudyUpException {
		int eventID = 1;
		Student student = new Student();
		Event event = eventServiceImpl.addStudentToEvent(student, eventID);
		Assertions.assertThrows(StudyUpException.class, () -> {
			eventServiceImpl.addStudentToEvent(student, eventID);
		  });	
		
	}
	/* 
	 * Bug found - Active events returns all events (including past events)
	 * active events should not include past events
	 */
	@Test
	void testifActiveEventReturns_PastEvent() throws StudyUpException{
		//This Event is an example of a past event
		Event event4 = new Event();
		event4.setEventID(4);
		Date past = new Date(20);
		event4.setDate(past);
		event4.setName("Event 4");
		DataStorage.eventData.put(event4.getEventID(), event4);
		List<Event> activeEventsFromFunct = new ArrayList<>();
		activeEventsFromFunct = eventServiceImpl.getActiveEvents();
		assertFalse(activeEventsFromFunct.contains(event4));
	}
	
}




