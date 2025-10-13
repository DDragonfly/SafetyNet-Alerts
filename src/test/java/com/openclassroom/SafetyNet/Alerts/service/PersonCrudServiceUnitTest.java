package com.openclassroom.SafetyNet.Alerts.service;

import com.openclassroom.SafetyNet.Alerts.model.Person;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PersonCrudServiceUnitTest {

    @Mock
    DataService dataService;
    @InjectMocks
    PersonCrudService personCrudService;

    @Test
    void getAll_returnsPersonsFromDataService() {
        var p = new Person();
        p.setFirstName("Chandler");
        when(dataService.getPersons()).thenReturn(List.of(p));
        assertThat(personCrudService.getAll()).hasSize(1);
        verify(dataService).getPersons();
    }

    @Test
    void create_update_delete_delegate() {
        var p = new Person();
        p.setFirstName("Monica");
        p.setLastName("Geller");

        when(dataService.addPerson(p)).thenReturn(true);
        assertThat(personCrudService.create(p)).isTrue();
        verify(dataService).addPerson(p);

        when(dataService.updatePerson("Monica", "Geller", p)).thenReturn(true);
        assertThat(personCrudService.update("Monica", "Geller", p)).isTrue();
        verify(dataService).updatePerson("Monica", "Geller", p);

        when(dataService.deletePerson("Monica", "Geller")).thenReturn(false);
        assertThat(personCrudService.delete("Monica", "Geller")).isFalse();
        verify(dataService).deletePerson("Monica", "Geller");
    }

}
