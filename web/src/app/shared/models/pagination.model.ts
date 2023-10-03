export interface Page<T> {
    totalElements: number;
    totalPages: number;
    number: number;
    content: T[];
}
