export type Activity = {
    id: string;
    type: string;
    createdAt: string;
};

export type UploadActivity = Activity & {
    type: "BOM_UPLOAD";
    tag: string;
    user: {
        name: string;
        email: string;
    };
};
