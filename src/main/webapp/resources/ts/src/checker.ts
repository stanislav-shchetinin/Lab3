export const checkPoint = (x: number, y: number, r: number) => {
    return y >= 0 && x >= 0 && r - x >= y ||
        y <= 0 && x <= 0 && x >= -r && y >= -r / 2 ||
        x <= 0 && y >= 0 && x * x + y * y <= (r / 2) * (r / 2);
}