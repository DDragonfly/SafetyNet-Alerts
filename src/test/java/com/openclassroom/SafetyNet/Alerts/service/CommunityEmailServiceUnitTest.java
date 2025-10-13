package com.openclassroom.SafetyNet.Alerts.service;

import com.openclassroom.SafetyNet.Alerts.model.Person;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommunityEmailServiceUnitTest {

    @Mock
    DataService dataService;
    @InjectMocks
    CommunityEmailService communityEmailService;

    @Test
    void getEmailsByCity_uniqueSorted_ignoreNulls_filterByCity() {
        when(dataService.getPersons()).thenReturn(List.of(
                person("New York", "a@gmail.com"),
                person("New York", "b@gmail.com"),
                person("New York", "b@gmail.com"), // duplicat!!
                person("New York", null),
                person("Los Angeles", "angeles@gmail.com")
        ));

        var emails = communityEmailService.getEmailsByCity("New York");

        assertThat(emails).containsExactly("a@gmail.com","b@gmail.com");
    }

    @Test
    void getEmailsByCity_empty_whenNoMatches() {
        when(dataService.getPersons()).thenReturn(List.of(person("Los Angeles", "angeles@gmail.com")));
        assertThat(communityEmailService.getEmailsByCity("New York")).isEmpty();
    }

    private static Person person(String city, String email) {
        var p = new Person();
        p.setCity(city);
        p.setEmail(email);
        return p;
    }
}
