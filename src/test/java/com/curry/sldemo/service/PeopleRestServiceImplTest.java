package com.curry.sldemo.service;

import com.curry.sldemo.model.EmailCharacterFrequency;
import com.curry.sldemo.model.PeopleResponseModel;
import com.curry.sldemo.model.PersonDuplicate;
import com.curry.sldemo.model.metadata.MetadataResponseModel;
import com.curry.sldemo.model.metadata.PagingMetadataResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@ExtendWith(MockitoExtension.class)
public class PeopleRestServiceImplTest {

    private static final int REQUESTED_PAGE = 1;
    private static final int PAGE_SIZE = 100;
    private static final String API_URL = "http://localhost";

    @Mock
    RestTemplate restTemplate;

    @Mock
    PeopleService peopleService;

    @InjectMocks
    private PeopleRestServiceImpl systemUnderTest;

    @BeforeEach
    public void init() {
        systemUnderTest = new PeopleRestServiceImpl(restTemplate, peopleService);
        systemUnderTest.apiUrl = API_URL;
        initMocks(this);
    }

    @Test
    public void getPeopleTest() {
        PeopleResponseModel expected = new PeopleResponseModel();
        ResponseEntity<PeopleResponseModel> responseModel = new ResponseEntity<>(expected, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class)))
                .thenReturn(responseModel);

        PeopleResponseModel actual = systemUnderTest.getPeople(REQUESTED_PAGE, PAGE_SIZE);
        assertEquals(actual, expected);
    }

    @Test
    public void getPeopleEmailCharacterFrequencyCountTest() {
        final int totalPages = 1;
        initializeRestTest(totalPages);

        List<EmailCharacterFrequency> frequencyMap = systemUnderTest.getPeopleEmailCharacterFrequencyCount();

        //verify rest call and service calls are only made a single time
        verify(restTemplate, times(1)).exchange(anyString(), any(HttpMethod.class), any(), any(Class.class));
        verify(peopleService, times(1)).getEmailCharacterFrequencyCountFromPeopleList(anyList());
        assertNotNull(frequencyMap);
    }

    @Test
    public void getPeopleEmailCharacterFrequencyCountTest_multiplePages() {
        final int totalPages = 2;
        initializeRestTest(totalPages);

        List<EmailCharacterFrequency> frequencyMap = systemUnderTest.getPeopleEmailCharacterFrequencyCount();

        //verify rest call is called twice (matching total number of pages), but service only once to get frequencies
        verify(restTemplate, times(2)).exchange(anyString(), any(HttpMethod.class), any(), any(Class.class));
        verify(peopleService, times(1)).getEmailCharacterFrequencyCountFromPeopleList(anyList());
        assertNotNull(frequencyMap);
    }

    @Test
    public void getPossibleDuplicatesTest() {
        final int totalPages = 1;
        initializeRestTest(totalPages);

        List<PersonDuplicate> duplicates = systemUnderTest.getPossibleDuplicates();

        //verify rest call and service calls are only made a single time
        verify(restTemplate, times(1)).exchange(anyString(), any(HttpMethod.class), any(), any(Class.class));
        verify(peopleService, times(1)).getPossibleDuplicatesFromList(anyList());
        assertNotNull(duplicates);
    }

    @Test
    public void getPossibleDuplicatesTest_multiplePages() {
        final int totalPages = 2;
        initializeRestTest(totalPages);

        List<PersonDuplicate> duplicates = systemUnderTest.getPossibleDuplicates();

        //verify rest call is called twice (matching total number of pages), but service only once to get duplicates
        verify(restTemplate, times(2)).exchange(anyString(), any(HttpMethod.class), any(), any(Class.class));
        verify(peopleService, times(1)).getPossibleDuplicatesFromList(anyList());
        assertNotNull(duplicates);
    }

    private void initializeRestTest(int totalPages) {
        PeopleResponseModel peopleResponseModel = mock(PeopleResponseModel.class);
        MetadataResponseModel metaDataResponseModel = mock(MetadataResponseModel.class);
        PagingMetadataResponseModel pagingMetadataResponseModel = mock(PagingMetadataResponseModel.class);

        when(pagingMetadataResponseModel.getTotalPages()).thenReturn(totalPages);
        when(metaDataResponseModel.getPaging()).thenReturn(pagingMetadataResponseModel);
        when(peopleResponseModel.getMetadataResponseModel()).thenReturn(metaDataResponseModel);

        ResponseEntity<PeopleResponseModel> responseModel = new ResponseEntity<>(peopleResponseModel, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class)))
                .thenReturn(responseModel);
    }
}
