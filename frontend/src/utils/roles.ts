export const MANAGEMENT_ROLES = ['MANAGER', 'DIRECTOR', 'CEO'] as const;

export const normalizeRole = (role?: string | null) => (role ?? 'EMPLOYEE').trim().toUpperCase();

export const hasManagementAccess = (role?: string | null) => MANAGEMENT_ROLES.includes(normalizeRole(role) as (typeof MANAGEMENT_ROLES)[number]);

export const canEdit = (role?: string | null) => hasManagementAccess(role);
