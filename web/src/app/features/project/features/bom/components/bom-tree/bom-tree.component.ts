import { FlatTreeControl } from "@angular/cdk/tree";
import { Component, OnDestroy, OnInit } from "@angular/core";
import { CollectionViewer, DataSource, SelectionChange } from "@angular/cdk/collections";
import { BehaviorSubject, map, merge, Observable, Subscription } from "rxjs";
import { Component as BomComponent } from "../../../../../../shared/models/bom.model";
import { BomService } from "../../../../../../shared/services/bom.service";

const sorted = (data: any[]) => data.sort((a, b) => Number(b.expandable) - Number(a.expandable));

class TreeNode {
    constructor(
        public item: BomComponent,
        public level = 1,
        public isLoading = false,
    ) {}

    get expandable(): boolean {
        return this.item.hasDependencies;
    }
}

class ComponentDataSource implements DataSource<TreeNode> {
    dataChange = new BehaviorSubject<TreeNode[]>([]);

    constructor(
        private bomService: BomService,
        private treeControl: FlatTreeControl<TreeNode>,
    ) {}

    get data(): TreeNode[] {
        return this.dataChange.value;
    }

    set data(value: TreeNode[]) {
        this.treeControl.dataNodes = value;
        this.dataChange.next(value);
    }

    connect(collectionViewer: CollectionViewer): Observable<TreeNode[]> {
        this.treeControl.expansionModel.changed.subscribe((change) => {
            if (change.added || change.removed) {
                this.handleTreeControl(change);
            }
        });
        return merge(collectionViewer.viewChange, this.dataChange).pipe(map(() => this.data));
    }

    disconnect(): void {}

    handleTreeControl(change: SelectionChange<TreeNode>) {
        if (change.added) {
            change.added.forEach((node) => this.toggleNode(node, true));
        }
        if (change.removed) {
            change.removed
                .slice()
                .reverse()
                .forEach((node) => this.toggleNode(node, false));
        }
    }

    toggleNode(node: TreeNode, expand: boolean) {
        const index = this.data.indexOf(node);
        if (!node.item.hasDependencies || index < 0) return;

        if (!expand) {
            let count = 0;
            for (let i = index + 1; i < this.data.length && this.data[i].level > node.level; i++, count++) {}
            this.data.splice(index + 1, count);
            this.dataChange.next(this.data);
            return;
        }

        node.isLoading = true;
        this.bomService.getComponent(node.item.id).subscribe((components) => {
            if (components && components.length) {
                const nodes = sorted(components.map((item) => new TreeNode(item, node.level + 1)));
                this.data.splice(index + 1, 0, ...nodes);
                this.dataChange.next(this.data);
            }

            node.isLoading = false;
        });
    }
}

@Component({
    selector: "va-project-bom-tree",
    templateUrl: "./bom-tree.component.html",
    styleUrls: ["./bom-tree.component.scss"],
})
export class BomTreeComponent implements OnInit, OnDestroy {
    displayedColumns: string[] = ["name", "version"];
    treeControl: FlatTreeControl<TreeNode>;
    dataSource: ComponentDataSource;
    isLoading: boolean = true;

    private sub: Subscription;

    constructor(private bomService: BomService) {
        this.treeControl = new FlatTreeControl<TreeNode>(this.getLevel, this.isExpandable);
        this.dataSource = new ComponentDataSource(bomService, this.treeControl);
    }

    getLevel = (node: TreeNode) => node.level;
    isExpandable = (node: TreeNode) => node.expandable;

    ngOnInit(): void {
        this.sub = this.bomService.listComponents("", 0, 100, true).subscribe((data) => {
            if (!data) return;
            const nodes: TreeNode[] = data.content.map((item: any) => new TreeNode(item, 0));
            this.dataSource.data = sorted(nodes);
            this.isLoading = false;
        });
    }

    ngOnDestroy(): void {
        this.sub.unsubscribe();
    }
}
