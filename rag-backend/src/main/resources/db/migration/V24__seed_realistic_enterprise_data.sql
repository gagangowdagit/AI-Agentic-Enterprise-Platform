TRUNCATE TABLE
    meeting_participants,
    meetings,
    notifications,
    memories,
    document_chunks,
    chunk_embeddings,
    documents,
    agent_executions,
    tasks,
    employees,
    projects,
    departments,
    users
RESTART IDENTITY CASCADE;

INSERT INTO users (id, name, email, password) VALUES
    (1, 'Avery Collins', 'avery.collins@northstar.io', 'admin123'),
    (2, 'Priya Nair', 'priya.nair@northstar.io', 'admin123'),
    (3, 'Daniel Brooks', 'daniel.brooks@northstar.io', 'admin123'),
    (4, 'Sofia Ramirez', 'sofia.ramirez@northstar.io', 'admin123');

INSERT INTO departments (id, name, description) VALUES
    (1, 'Platform Engineering', 'Owns the core platform architecture, CI/CD automation, reliability, and internal developer tooling.'),
    (2, 'Product Strategy', 'Defines product direction, customer value, and roadmap prioritization across the enterprise portfolio.'),
    (3, 'Operations', 'Coordinates delivery execution, rollout planning, service health, and cross-functional coordination.'),
    (4, 'Finance & Risk', 'Oversees budget allocation, financial planning, risk controls, and commercial performance review.');

INSERT INTO projects (id, name, status, description, start_date, end_date, priority, assigned_to) VALUES
    (1, 'Northstar Commerce Upgrade', 'Active', 'Modernize the commerce stack for faster checkout flows, stronger pricing logic, and resilient payment orchestration.', '2025-08-10', '2026-04-26', 'HIGH', 1),
    (2, 'Customer Insights Portal', 'Active', 'Launch a unified executive analytics experience to consolidate sales, retention, and product usage insights.', '2025-06-19', '2026-03-18', 'HIGH', 2),
    (3, 'Service Reliability Program', 'Pending', 'Improve uptime, SLO governance, and operational playbooks across the support and delivery landscape.', '2025-09-01', '2026-05-30', 'MEDIUM', 3),
    (4, 'Enterprise Knowledge Hub', 'Completed', 'Consolidate product documentation, onboarding guides, and process runbooks into a searchable workspace.', '2025-02-01', '2025-11-15', 'MEDIUM', 2),
    (5, 'Budget Control Automation', 'Active', 'Automate approvals, cost tracking, and variance analysis across finance and operational spend categories.', '2025-07-12', '2026-02-28', 'HIGH', 4);

INSERT INTO employees (id, first_name, last_name, email, role, department_id, project_id) VALUES
    (1, 'Avery', 'Collins', 'avery.collins@northstar.io', 'Engineering Director', 1, 1),
    (2, 'Nina', 'Patel', 'nina.patel@northstar.io', 'Senior Platform Engineer', 1, 1),
    (3, 'Marcus', 'Hughes', 'marcus.hughes@northstar.io', 'DevOps Engineer', 1, 1),
    (4, 'Leah', 'Kim', 'leah.kim@northstar.io', 'Product Manager', 2, 2),
    (5, 'Jon', 'Morris', 'jon.morris@northstar.io', 'UX Research Lead', 2, 2),
    (6, 'Harper', 'Singh', 'harper.singh@northstar.io', 'Operations Manager', 3, 3),
    (7, 'Omar', 'Lopez', 'omar.lopez@northstar.io', 'SRE Engineer', 3, 3),
    (8, 'Claire', 'Watson', 'claire.watson@northstar.io', 'Knowledge Manager', 2, 4),
    (9, 'Tariq', 'Ahmed', 'tariq.ahmed@northstar.io', 'Finance Lead', 4, 5),
    (10, 'Ella', 'Stone', 'ella.stone@northstar.io', 'Analyst', 4, 5),
    (11, 'Parker', 'Green', 'parker.green@northstar.io', 'Release Manager', 1, NULL),
    (12, 'Maya', 'Johnson', 'maya.johnson@northstar.io', 'Business Analyst', 3, NULL),
    (13, 'Rafael', 'Ibarra', 'rafael.ibarra@northstar.io', 'Solutions Architect', 1, 2),
    (14, 'Amelia', 'Scott', 'amelia.scott@northstar.io', 'Program Coordinator', 3, 4),
    (15, 'Lucas', 'Nguyen', 'lucas.nguyen@northstar.io', 'Data Engineer', 2, 2),
    (16, 'Priya', 'Dutta', 'priya.dutta@northstar.io', 'Risk Analyst', 4, NULL),
    (17, 'Samuel', 'Okafor', 'samuel.okafor@northstar.io', 'Support Lead', 3, 3),
    (18, 'Isla', 'Bennett', 'isla.bennett@northstar.io', 'Product Analyst', 2, 4);

INSERT INTO tasks (id, project_id, assigned_user_id, title, description, status, priority) VALUES
    ('TASK-1001', 1, 1, 'Finalize payment gateway fallback', 'Harden checkout failover for gateway downtime and timeout recovery.', 'In Progress', 'HIGH'),
    ('TASK-1002', 1, 2, 'Stabilize checkout latency', 'Reduce API latency for pricing and inventory calls during peak traffic.', 'Pending', 'HIGH'),
    ('TASK-1003', 2, 2, 'Conference roadmap signoff', 'Align roadmap review with leadership stakeholders and capture final go-live scope.', 'Completed', 'MEDIUM'),
    ('TASK-1004', 2, 3, 'Dashboard QA validation', 'Validate sales funnel metrics across executive, regional, and product views.', 'In Progress', 'HIGH'),
    ('TASK-1005', 3, 3, 'SLO governance update', 'Lock in reliability objectives and reporting cadence for the next quarter.', 'Pending', 'MEDIUM'),
    ('TASK-1006', 3, 4, 'MC incident drill', 'Run the cross-functional incident rehearsal and document findings.', 'Completed', 'MEDIUM'),
    ('TASK-1007', 4, 2, 'Knowledge archive cleanup', 'Review legacy onboarding docs and remove outdated process references.', 'Completed', 'LOW'),
    ('TASK-1008', 5, 4, 'Budget variance review', 'Validate planned spend vs variance thresholds for the next reporting cycle.', 'In Progress', 'HIGH');

INSERT INTO notifications (id, user_id, type, title, message, is_read, created_at) VALUES
    (1, 1, 'deadline', 'Checkout milestone review', 'Payment fallback review is scheduled for Friday at 4:00 PM.', false, '2026-09-18 09:00:00'),
    (2, 2, 'info', 'Portal design update', 'Customer Insights feedback has been incorporated into Q4 prioritization.', false, '2026-09-19 13:30:00'),
    (3, 4, 'risk', 'Budget threshold alert', 'Automation spend is trending 12% above the approved forecast this month.', false, '2026-09-20 08:45:00'),
    (4, 3, 'success', 'Reliability audit shared', 'The SRE audit notes have been circulated to the operations leadership group.', true, '2026-09-17 16:10:00');

INSERT INTO memories (id, project_id, content, memory_type, created_at) VALUES
    ('mem-001', 1, 'Payment fallback needs to be validated before the next release candidate. We should keep cutover risk documentation in the customer-facing runbook.', 'decision', '2026-09-07 14:45:00'),
    ('mem-002', 2, 'Leadership wants the analytics launch tied to revenue enablement metrics and not just operational platform health.', 'insight', '2026-09-09 11:20:00'),
    ('mem-003', 3, 'Incident review confirmed that our alert routing needs to be consolidated to reduce false positives during peak hours.', 'lesson', '2026-09-11 09:55:00'),
    ('mem-004', 5, 'Finance approval workflow should route exceptions to product and ops leads before final amendment.', 'decision', '2026-09-12 10:40:00');

INSERT INTO meetings (id, title, description, project_id, project_name, meeting_date, start_time, end_time, participant_count, status, google_meet_url, transcript_text, transcript_file_name, transcript_uploaded_at, ai_summary, ai_action_items, ai_decisions, created_by, created_at, updated_at, agenda) VALUES
    (1, 'Commerce Upgrade Planning', 'Sprint planning and release readiness walkthrough for the Northstar commerce upgrade.', '1', 'Northstar Commerce Upgrade', '2026-09-23', '09:00', '10:00', 6, 'Scheduled', 'https://meet.google.com/abc-defg-hij', 'We reviewed the fallback strategy, pricing changes, and release dependency map. The team confirmed the payment gateway failover test should pass before launch. We also aligned on the rollout window for the pricing service and noted that the analytics dashboard needs final QA validation.', 'commerce-planning-transcript.txt', '2026-09-22 18:15:00', 'The team confirmed readiness for the payment fallback and prioritized release validation across pricing and checkout services. Risk is concentrated around time-of-day spikes and edge-case failover behavior.', '1. Complete final gateway failover rehearsal\n2. Lock pricing service rollout window\n3. Confirm QA sign-off for checkout banner updates', '1. Release window will stay on a low-traffic morning slot\n2. Fallback path needs a documented rollback plan', 'system', '2026-09-20 11:00:00', '2026-09-22 18:15:00', 'Finalize payment fallback\nReview pricing edge cases\nQA sign-off for checkout flow'),
    (2, 'Customer Portal Review', 'Leadership review for the customer insights portal and dashboard KPI alignment.', '2', 'Customer Insights Portal', '2026-09-25', '14:00', '15:00', 5, 'Scheduled', 'https://meet.google.com/xyz-abcd-wxy', 'The product and design leads reviewed the cumulative metrics model and agreed to keep the executive dashboard limited to high-signal KPIs. The engineering team flagged a need for data freshness checks before launch.', 'portal-review-transcript.txt', '2026-09-24 15:10:00', 'The group agreed to prioritize high-signal executive metrics and add data freshness warnings before the launch. Engineering will complete QA for the pipeline before the beta cutover.', '1. Finalize KPI set\n2. Validate data latency checks\n3. Move beta launch to the next milestone', '1. Executive dashboard will use only blended revenue and retention metrics\n2. Data freshness warnings will be in the first release', 'system', '2026-09-21 08:50:00', '2026-09-24 15:10:00', 'Confirm KPI definitions\nReview data freshness model\nApprove beta launch checklist'),
    (3, 'Risk and Spend Controls', 'Quarterly budget control session for the automation and enterprise support roadmap.', '5', 'Budget Control Automation', '2026-09-27', '11:30', '12:30', 4, 'Scheduled', 'https://meet.google.com/def-ghij-klm', 'The finance and operations teams reviewed automation spend variance and signaled a need for stronger approvals for exceptions. Product and ops advised that control thresholds should trigger before overspend crosses the monthly forecast line.', 'budget-control-transcript.txt', '2026-09-26 13:00:00', 'The team aligned on threshold-based approval gates and a monthly forecast variance review. The plan is to trigger exceptions earlier and link spend to project ROI when thresholds are crossed.', '1. Add exception approval routing\n2. Review variance thresholds\n3. Publish monthly spend view', '1. Approval workflow will include BOTH finance and ops leads\n2. Reporting becomes part of the monthly close process', 'system', '2026-09-22 12:20:00', '2026-09-26 13:00:00', 'Review automation spend\nFinalize approval routing\nApprove monthly variance rule');

INSERT INTO meeting_participants (meeting_id, employee_id) VALUES
    (1, 1), (1, 2), (1, 3), (1, 4), (1, 11), (1, 13),
    (2, 4), (2, 5), (2, 13), (2, 15), (2, 18),
    (3, 9), (3, 10), (3, 12), (3, 16);

INSERT INTO documents (id, project_id, file_name, file_type, file_size, file_path, uploaded_at, extracted_text) VALUES
    (1, 1, 'northstar-commerce-brief.txt', 'text/plain', 4120, 'E:/AI-Agentic-Enterprise-Platform/rag-backend/seed-documents/project-1/northstar-commerce-brief.txt', '2026-09-18 15:30:00', 'Northstar Commerce Upgrade summary: Price recalculation logic and payment failover must be validated prior to peak season. Dependencies include the pricing service, gateway integration, and customer support notifications.'),
    (2, 2, 'customer-insights-portal.txt', 'text/plain', 3825, 'E:/AI-Agentic-Enterprise-Platform/rag-backend/seed-documents/project-2/customer-insights-portal.txt', '2026-09-19 10:10:00', 'Customer Insights Portal overview: Dashboard improvements should focus on revenue, retention, and product adoption. Data freshness and executive KPI definitions will be finalized in the next stakeholder review.'),
    (3, 3, 'service-reliability-plan.txt', 'text/plain', 3810, 'E:/AI-Agentic-Enterprise-Platform/rag-backend/seed-documents/project-3/service-reliability-plan.txt', '2026-09-20 12:00:00', 'Service Reliability Program baseline: monitor SLOs, stabilize alert routing, and validate runbooks for the next quarter. Priorities are automation, incident rehearsal, and service ownership mapping.'),
    (4, 4, 'knowledge-hub-overview.txt', 'text/plain', 2940, 'E:/AI-Agentic-Enterprise-Platform/rag-backend/seed-documents/project-4/knowledge-hub-overview.txt', '2026-09-21 08:20:00', 'Enterprise Knowledge Hub intake: Document taxonomy, onboarding guides, and operational playbooks are archived in a central repository. The final scope includes reusable templates and easy search by department.'),
    (5, 5, 'budget-control-automation.txt', 'text/plain', 4300, 'E:/AI-Agentic-Enterprise-Platform/rag-backend/seed-documents/project-5/budget-control-automation.txt', '2026-09-18 09:45:00', 'Budget Control Automation scope: automate approval routing, capture variance thresholds, and produce monthly spend insight reports. Finance and operations will review exception paths before the next close cycle.');

INSERT INTO agent_executions (execution_id, project_id, status, start_time, end_time, current_step) VALUES
    ('exec-001', 1, 'completed', '2026-09-16 09:00:00', '2026-09-16 09:14:00', 'Final review'),
    ('exec-002', 2, 'running', '2026-09-20 11:20:00', NULL, 'Data pipeline validation');

INSERT INTO document_chunks (id, document_id, content, chunk_index) VALUES
    (1, 1, 'Northstar Commerce Upgrade summary: Price recalculation logic and payment failover must be validated prior to peak season.', 1),
    (2, 2, 'Customer Insights Portal overview: Dashboard improvements should focus on revenue, retention, and product adoption.', 1),
    (3, 5, 'Budget Control Automation scope: automate approval routing, capture variance thresholds, and produce monthly spend insight reports.', 1);

INSERT INTO chunk_embeddings (id, chunk_id, embedding) VALUES
    (1, 1, 'seeded-embedding-northstar-commerce'),
    (2, 2, 'seeded-embedding-customer-insights'),
    (3, 3, 'seeded-embedding-budget-control');
