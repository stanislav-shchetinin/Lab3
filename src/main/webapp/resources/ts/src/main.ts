import {Graph} from "./Graph";
import {Point} from "./Point";
import {checkPoint} from "./checker";

const defaultRadius : number = 1;
export const graph = new Graph();
graph.redrawAll(defaultRadius);

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

const canvas : HTMLElement = document.querySelector('#graph');
canvas.addEventListener('mousedown', function(event) {
    const selectorR : HTMLInputElement = document.getElementById("form:r") as HTMLInputElement | null;
    const rect = this.getBoundingClientRect()
    const x = (event.clientX - rect.left - 200)/35;
    const y = (event.clientY - rect.top - 200)/35;
    const r = Number(selectorR.value);
    console.log(r);
    graph.drawPoint(x, -y, checkPoint(x, -y, r));
})