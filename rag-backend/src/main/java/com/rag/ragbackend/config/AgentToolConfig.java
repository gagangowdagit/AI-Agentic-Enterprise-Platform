package com.rag.ragbackend.config;

import com.rag.ragbackend.agent.CreateTaskTool;
import com.rag.ragbackend.agent.GetProjectTasksTool;
import com.rag.ragbackend.agent.ProjectInfoTool;
import com.rag.ragbackend.agent.RagSearchTool;
import com.rag.ragbackend.agent.ToolRegistry;
import com.rag.ragbackend.agent.UpdateTaskTool;
import com.rag.ragbackend.processing.RagService;
import com.rag.ragbackend.repository.ProjectRepository;
import com.rag.ragbackend.service.ProjectService;
import com.rag.ragbackend.service.TaskService;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentToolConfig {

    private final ToolRegistry toolRegistry;
    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final RagService ragService;
    private final TaskService taskService;

    public AgentToolConfig(
            ToolRegistry toolRegistry,
            ProjectService projectService,
            ProjectRepository projectRepository,
            RagService ragService,
            TaskService taskService) {
        this.toolRegistry = toolRegistry;
        this.projectService = projectService;
        this.projectRepository = projectRepository;
        this.ragService = ragService;
        this.taskService = taskService;
    }

    @PostConstruct
    public void registerTools() {
        toolRegistry.register(new ProjectInfoTool(projectService, projectRepository));
        toolRegistry.register(new RagSearchTool(ragService));
        toolRegistry.register(new CreateTaskTool(taskService));
        toolRegistry.register(new UpdateTaskTool(taskService));
        toolRegistry.register(new GetProjectTasksTool(taskService));
    }
}