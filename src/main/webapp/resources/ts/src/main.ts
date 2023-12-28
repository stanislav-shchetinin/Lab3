import {Graph} from "./Graph";
import {Point} from "./Point";
export const graph = new Graph();
graph.redrawAll(3);

interface Window {
    redrawCanvas: (points : Point[], radius: number) => void;
    changeRadius : (radius: number) => void;
}

declare const window: Window;

window.redrawCanvas = (points : Point[], radius: number) => {
    graph.redrawAll(radius);
    points.forEach(point => graph.drawPoint(point.x, point.y, point.res))
}

window.changeRadius = (radius: number) => {
    graph.redrawAll(radius);
}