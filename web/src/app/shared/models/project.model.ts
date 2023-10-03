import { Bom } from "./bom.model";

export interface Project {
    id: string;
    name: string;
    slug: string;
    main?: Bom;
    createdAt: string;
    updatedAt: string;
    archivedAt?: string;
}
