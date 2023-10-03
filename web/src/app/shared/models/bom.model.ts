export interface Bom {
    id: string;
    tag: string;
    format?: string;
    formatVersion?: string;
    dataFormat?: string;
    metrics: Metrics;
    history: History[];
    createdAt: string;
    updatedAt: string;
}

export interface Package {
    id: string;
    licenses: string[];
    latestVersion?: string;
    links: {
        label: string;
        url: string;
    }[];
}

export interface Component {
    id: string;
    name: string;
    version?: string;
    hasDependencies: boolean;
    dependencies: Component[];
    packageURL: string;
    cpe: string;
    swid: string;
    fixedVersion?: string;
    package?: Package;
    risk?: {
        vulnerabilityId: string;
        risk: number;
    };
}

export interface History {
    date: string;
    components: number;
}

export interface Metrics {
    totalDependencies: number;
    updatesCount: number;
    dependencyCount: {
        [ecosystem: string]: number;
    };
    riskLowCount: number;
    riskMediumCount: number;
    riskHighCount: number;
    riskCriticalCount: number;
}
