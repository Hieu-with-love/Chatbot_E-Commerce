package hcmute.edu.vn.chatbot_ec.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.chatbot_ec.model.ChatSessionResponse;
import hcmute.edu.vn.chatbot_ec.model.ChatSessionStartRequest;
import hcmute.edu.vn.chatbot_ec.model.Message;
import hcmute.edu.vn.chatbot_ec.model.MessageResponse;
import hcmute.edu.vn.chatbot_ec.model.MessageSendRequest;
import hcmute.edu.vn.chatbot_ec.network.ChatbotApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RunWith(MockitoJUnitRunner.class)
public class ChatRepositoryTest {

    @Mock
    private ChatbotApiService mockApiService;
    
    private ChatRepository chatRepository;
    
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        chatRepository = new ChatRepository(mockApiService); // Assuming we update constructor to accept service
    }
    
    @Test
    public void testStartChatSession() {
        // Create a mock Call object
        Call<ChatSessionResponse> mockCall = mock(Call.class);
        
        // Setup the mock API to return our mock Call
        when(mockApiService.startChatSession(any(ChatSessionStartRequest.class))).thenReturn(mockCall);
        
        // Create the test data
        int userId = 1;
        String initialMessage = "Hello";
        
        // Create a mock callback
        ChatRepository.ChatCallback<ChatSessionResponse> mockCallback = mock(ChatRepository.ChatCallback.class);
        
        // Call the method under test
        chatRepository.startChatSession(userId, initialMessage, mockCallback);
        
        // Capture the request that was sent
        ArgumentCaptor<ChatSessionStartRequest> requestCaptor = ArgumentCaptor.forClass(ChatSessionStartRequest.class);
        verify(mockApiService).startChatSession(requestCaptor.capture());
        
        // Verify the request was constructed correctly
        ChatSessionStartRequest capturedRequest = requestCaptor.getValue();
        assertEquals(userId, capturedRequest.getUserId().intValue());
        assertEquals(initialMessage, capturedRequest.getInitialMessage());
        
        // Verify that enqueue was called on the Call object
        verify(mockCall).enqueue(any(Callback.class));
    }
    
    @Test
    public void testProcessMessage() {
        // Create a mock Call object
        Call<MessageResponse> mockCall = mock(Call.class);
        
        // Setup the mock API to return our mock Call
        when(mockApiService.processMessage(any(MessageSendRequest.class))).thenReturn(mockCall);
        
        // Create the test data
        int sessionId = 1;
        String content = "How are you?";
        int userId = 1;
        
        // Create a mock callback
        ChatRepository.ChatCallback<MessageResponse> mockCallback = mock(ChatRepository.ChatCallback.class);
        
        // Call the method under test
        chatRepository.processMessage(sessionId, content, userId, mockCallback);
        
        // Capture the request that was sent
        ArgumentCaptor<MessageSendRequest> requestCaptor = ArgumentCaptor.forClass(MessageSendRequest.class);
        verify(mockApiService).processMessage(requestCaptor.capture());
        
        // Verify the request was constructed correctly
        MessageSendRequest capturedRequest = requestCaptor.getValue();
        assertEquals(sessionId, capturedRequest.getSessionId().intValue());
        assertEquals(content, capturedRequest.getContent());
        assertEquals(userId, capturedRequest.getUserId().intValue());
        
        // Verify that enqueue was called on the Call object
        verify(mockCall).enqueue(any(Callback.class));
    }
    
    // Similar tests for other methods like getChatSession, getChatSessionsByUser, endChatSession, etc.
}
