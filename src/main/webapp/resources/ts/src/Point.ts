export class Point {
    private _x: number;
    private _y: number;
    private _res: boolean;

    get y(): number {
        return this._y;
    }
    get x(): number {
        return this._x;
    }

    get res(): boolean {
        return this._res;
    }

}