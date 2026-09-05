package com.rag.ragbackend.memory;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MemoryServiceTest {

    @Test
    void savesMemoryWithProjectAssociation() {
        MemoryRepository repository = mock(MemoryRepository.class);
        MemoryService service = new MemoryServiceImpl(repository);
        Memory memory = new Memory("memory-1", "project-1", "User prefers concise answers.", "PREFERENCE", LocalDateTime.now());
        when(repository.save(memory)).thenReturn(memory);

        Memory saved = service.saveMemory(memory);

        assertEquals(memory, saved);
        assertEquals("project-1", saved.getProjectId());
        verify(repository).save(memory);
    }

    @Test
    void assignsIdAndCreatedTimestampWhenMissing() {
        MemoryRepository repository = mock(MemoryRepository.class);
        MemoryService service = new MemoryServiceImpl(repository);
        Memory memory = new Memory();
        memory.setProjectId("project-1");
        memory.setContent("Important project detail.");
        memory.setMemoryType("FACT");
        when(repository.save(memory)).thenReturn(memory);

        service.saveMemory(memory);

        assertNotNull(memory.getId());
        assertNotNull(memory.getCreatedAt());
        verify(repository).save(memory);
    }

    @Test
    void retrievesMemoriesByProject() {
        MemoryRepository repository = mock(MemoryRepository.class);
        MemoryService service = new MemoryServiceImpl(repository);
        List<Memory> expected = List.of(new Memory("memory-1", "project-1", "A", "FACT", LocalDateTime.now()));
        when(repository.findByProjectIdOrderByCreatedAtAsc("project-1")).thenReturn(expected);

        List<Memory> actual = service.getMemoriesByProjectId("project-1");

        assertEquals(expected, actual);
        verify(repository).findByProjectIdOrderByCreatedAtAsc("project-1");
    }

    @Test
    void retrievesMemoriesMatchingProjectAndQueryTerms() {
        MemoryRepository repository = mock(MemoryRepository.class);
        MemoryService service = new MemoryServiceImpl(repository);
        Memory strongestMatch = new Memory("memory-1", "project-1", "The contract renews annually.", "FACT", LocalDateTime.now());
        Memory weakerMatch = new Memory("memory-2", "project-1", "The contract has a fixed budget.", "FACT", LocalDateTime.now());
        Memory unrelated = new Memory("memory-3", "project-1", "The team prefers morning meetings.", "PREFERENCE", LocalDateTime.now());
        when(repository.findByProjectIdOrderByCreatedAtAsc("project-1"))
                .thenReturn(List.of(weakerMatch, unrelated, strongestMatch));

        List<Memory> actual = service.getRelevantMemories("project-1", "contract renews");

        assertEquals(List.of(strongestMatch, weakerMatch), actual);
    }

    @Test
    void rejectsInvalidMemoryInput() {
        MemoryService service = new MemoryServiceImpl(mock(MemoryRepository.class));

        assertThrows(IllegalArgumentException.class, () -> service.saveMemory(null));
        assertThrows(IllegalArgumentException.class, () -> service.saveMemory(new Memory("", "content", "FACT")));
        assertThrows(IllegalArgumentException.class, () -> service.saveMemory(new Memory("project-1", "", "FACT")));
        assertThrows(IllegalArgumentException.class, () -> service.getMemoriesByProjectId(" "));
    }
}