import { Project } from "./project.model";
import { Vulnerability } from "./vulnerability.model";

export type Status = {
    unread: number;
};

export type Notification = {
    id: string;
    type: string;
    read: boolean;
    createdAt: string;
    project?: Project;
    vulnerabilities?: Vulnerability[];
};
