import io.restassured.response.Response;
import org.hamcrest.core.*;
import org.junit.Test;
import pkg.BookerClient;
import pkg.Booking;
import pkg.BookingHeaders;
import pkg.FullBooking;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class demoTests {
    @Test
    public void createBooking() {
        Booking postBookingBody = Booking.setPostBookingBody();
        BookingHeaders postBookingHeaders = BookingHeaders.setPostBookingHeaders();

        BookerClient bookerClient = new BookerClient();
        Response booking = bookerClient.createBooking(postBookingBody, postBookingHeaders);
        assertThat(booking.statusCode(), IsEqual.equalTo(200));

    }

    @Test
    public void getBooking() {
        BookingHeaders getBookingHeaders = BookingHeaders.setGetBookingHeaders();

        BookerClient bookerClient = new BookerClient();
        Response getBookingResponse = bookerClient.getBooking(getBookingHeaders, "1");

        getBookingResponse.
        then().
            assertThat().statusCode(200);

    }

    @Test
    public void postAuth() {
        BookingHeaders authHeaders = BookingHeaders.setPostAuthHeaders();

        String userName = "admin";
        String pwd = "password123";

        BookerClient bookerClient = new BookerClient();
        Response authToken = bookerClient.createAuthToken(authHeaders, userName, pwd);
        String body = authToken.body().asString();
        System.out.println(body);
        assertThat(authToken.statusCode(), IsEqual.equalTo(200));

    }

    @Test
    public void putBooking() {
        BookerClient bookerClient = new BookerClient();

        // 1. create an auth token - done
        String token = Booking.createAuthToken(bookerClient);

        // 2. create a booking
        FullBooking fullBooking = bookerClient.createBooking(
                Booking.setPostBookingBody(),
                BookingHeaders.setPostBookingHeaders()).as(FullBooking.class);

        // 3. modify the booking
        fullBooking.getBooking().setLastname("Harknabrig");

        // 4. get the headers specifically for PUT booking then
        BookingHeaders updateBookingHeaders = BookingHeaders.setPutBookingHeaders(token);

        // 5. include what you got from #3 into the updateBooking method
        Response putBookingResponse = bookerClient.updateBooking(updateBookingHeaders, fullBooking);

        System.out.println(putBookingResponse.body().asString());

        // 6. log the response and include a simple status code assertion
        putBookingResponse.
        then().
            assertThat().
                statusCode(200);
    }

    @Test
    public void deleteBooking() {
        BookerClient bookerClient = new BookerClient();

        String token = Booking.createAuthToken(bookerClient);

        FullBooking fullBooking = bookerClient.createBooking(
                    Booking.setPostBookingBody(),
                    BookingHeaders.setPostBookingHeaders())
                .as(FullBooking.class);

        BookingHeaders deleteBookingHeaders =
                BookingHeaders.setDeleteBookingHeaders(token);

        // delete the booking
        bookerClient.deleteBooking(deleteBookingHeaders,
                              fullBooking.getBookingid());

        Response getBookingResponse = bookerClient.
                getBooking(BookingHeaders.setGetBookingHeaders(),
                           fullBooking.getBookingid().toString());

        getBookingResponse.
        then().
            assertThat().statusCode(404);
    }

    @Test
    public void postAuth_nullPassword() {
        BookingHeaders authHeaders = BookingHeaders.setPostAuthHeaders();

        String userName = "admin";
        String pwd = null;

        BookerClient bookerClient = new BookerClient();
        Response authToken = bookerClient.createAuthToken(authHeaders, userName, pwd);
        String body = authToken.body().asString();
        System.out.println(body);
        assertThat(authToken.statusCode(), is((200)));

    }


    @Test
    public void createBooking_badCheckinDates_invalidDate() {
        Booking postBookingBody = Booking.setPostBookingBody();
        postBookingBody.getBookingdates().setCheckin("32-32-2050");
        BookingHeaders postBookingHeaders = BookingHeaders.setPostBookingHeaders();

        BookerClient bookerClient = new BookerClient();
        Response booking = bookerClient.createBooking(postBookingBody, postBookingHeaders);
        System.out.println(booking.body().asString());
        booking.then().assertThat().statusCode(is(200));
    }
}
