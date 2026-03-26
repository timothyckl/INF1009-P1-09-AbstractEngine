package com.p1_7.game.maze;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.p1_7.abstractengine.render.IDrawContext;
import com.p1_7.abstractengine.render.IRenderable;
import com.p1_7.abstractengine.transform.ITransform;
import com.p1_7.game.platform.GdxDrawContext;
import com.p1_7.game.spatial.Transform2D;

/**
 * renders the maze wall layer from a {@link MazeWallGrid}.
 *
 * the renderer draws the dark wall body first, then overlays edge and corner
 * tiles from the wall atlas on the exposed perimeter only.
 */
public final class MazeWallRenderer {

    private static final Color WALL_FILL_COLOUR = new Color(0.07f, 0.10f, 0.16f, 1f);

    private static final String WALL_TILESET_ASSET = "walls/tilemap.png";
    private static final int WALL_TILE_SRC_SIZE = 8;
    private static final int WALL_TILE_SPACING = 1;
    private static final float WALL_TILE_DRAW_SIZE = 16f;

    private static final int[] WALL_TOP_LEFT_CORNER_TILE = { 0, 0 };
    private static final int[] WALL_HORIZONTAL_TILE = { 1, 0 };
    private static final int[] WALL_TOP_RIGHT_CORNER_TILE = { 3, 0 };
    private static final int[] WALL_LEFT_VERTICAL_TILE = { 0, 1 };
    private static final int[] WALL_RIGHT_VERTICAL_TILE = { 3, 1 };
    private static final int[] WALL_BOTTOM_LEFT_CORNER_TILE = { 0, 2 };
    private static final int[] WALL_BOTTOM_RIGHT_CORNER_TILE = { 3, 2 };

    /**
     * builds a renderable for the entire wall layer.
     *
     * @param wallGrid wall grid derived from the maze layout
     * @return renderable for the full wall layer
     */
    public IRenderable createRenderable(MazeWallGrid wallGrid) {
        if (wallGrid == null) {
            throw new IllegalArgumentException("wallGrid must not be null");
        }

        List<MazeWallGrid.WallCell> cells = new ArrayList<>(wallGrid.getWallCells());
        List<HorizontalSegment> topSegments = collectHorizontalSegments(cells, wallGrid, true);
        List<HorizontalSegment> bottomSegments = collectHorizontalSegments(cells, wallGrid, false);
        List<VerticalSegment> leftSegments = collectVerticalSegments(cells, wallGrid, true);
        List<VerticalSegment> rightSegments = collectVerticalSegments(cells, wallGrid, false);
        Transform2D bounds = computeBounds(cells);

        return new IRenderable() {
            @Override public String getAssetPath() { return WALL_TILESET_ASSET; }
            @Override public ITransform getTransform() { return bounds; }

            @Override
            public void render(IDrawContext ctx) {
                GdxDrawContext gdx = (GdxDrawContext) ctx;

                for (MazeWallGrid.WallCell cell : cells) {
                    gdx.drawTintedQuad(
                        WALL_FILL_COLOUR, cell.getX(), cell.getY(), cell.getWidth(), cell.getHeight());
                }

                for (HorizontalSegment segment : topSegments) {
                    drawHorizontalEdge(gdx, WALL_HORIZONTAL_TILE,
                        segment.start, segment.end, segment.line - WALL_TILE_DRAW_SIZE);
                }
                for (HorizontalSegment segment : bottomSegments) {
                    drawHorizontalEdge(gdx, WALL_HORIZONTAL_TILE,
                        segment.start, segment.end, segment.line);
                }
                for (VerticalSegment segment : leftSegments) {
                    drawVerticalEdge(gdx, WALL_LEFT_VERTICAL_TILE,
                        segment.line, segment.start, segment.end);
                }
                for (VerticalSegment segment : rightSegments) {
                    drawVerticalEdge(gdx, WALL_RIGHT_VERTICAL_TILE,
                        segment.line - WALL_TILE_DRAW_SIZE, segment.start, segment.end);
                }

                for (MazeWallGrid.WallCell cell : cells) {
                    if (wallGrid.isTopEdgeExposed(cell) && wallGrid.isLeftEdgeExposed(cell)) {
                        drawAtlasTile(gdx, WALL_TOP_LEFT_CORNER_TILE,
                            cell.getX(),
                            cell.getY() + cell.getHeight() - WALL_TILE_DRAW_SIZE,
                            WALL_TILE_DRAW_SIZE, WALL_TILE_DRAW_SIZE);
                    }
                    if (wallGrid.isTopEdgeExposed(cell) && wallGrid.isRightEdgeExposed(cell)) {
                        drawAtlasTile(gdx, WALL_TOP_RIGHT_CORNER_TILE,
                            cell.getX() + cell.getWidth() - WALL_TILE_DRAW_SIZE,
                            cell.getY() + cell.getHeight() - WALL_TILE_DRAW_SIZE,
                            WALL_TILE_DRAW_SIZE, WALL_TILE_DRAW_SIZE);
                    }
                    if (wallGrid.isBottomEdgeExposed(cell) && wallGrid.isLeftEdgeExposed(cell)) {
                        drawAtlasTile(gdx, WALL_BOTTOM_LEFT_CORNER_TILE,
                            cell.getX(), cell.getY(), WALL_TILE_DRAW_SIZE, WALL_TILE_DRAW_SIZE);
                    }
                    if (wallGrid.isBottomEdgeExposed(cell) && wallGrid.isRightEdgeExposed(cell)) {
                        drawAtlasTile(gdx, WALL_BOTTOM_RIGHT_CORNER_TILE,
                            cell.getX() + cell.getWidth() - WALL_TILE_DRAW_SIZE,
                            cell.getY(),
                            WALL_TILE_DRAW_SIZE, WALL_TILE_DRAW_SIZE);
                    }
                }
            }
        };
    }

    private static Transform2D computeBounds(List<MazeWallGrid.WallCell> cells) {
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE;
        float maxY = -Float.MAX_VALUE;
        for (MazeWallGrid.WallCell cell : cells) {
            minX = Math.min(minX, cell.getX());
            minY = Math.min(minY, cell.getY());
            maxX = Math.max(maxX, cell.getX() + cell.getWidth());
            maxY = Math.max(maxY, cell.getY() + cell.getHeight());
        }
        return new Transform2D(minX, minY, maxX - minX, maxY - minY);
    }

    private static List<HorizontalSegment> collectHorizontalSegments(List<MazeWallGrid.WallCell> cells,
                                                                     MazeWallGrid wallGrid,
                                                                     boolean top) {
        List<EdgeRun> runs = new ArrayList<>();
        for (MazeWallGrid.WallCell cell : cells) {
            boolean exposed = top ? wallGrid.isTopEdgeExposed(cell) : wallGrid.isBottomEdgeExposed(cell);
            if (!exposed) {
                continue;
            }
            runs.add(new EdgeRun(
                top ? cell.getY() + cell.getHeight() : cell.getY(),
                cell.getX(),
                cell.getX() + cell.getWidth()
            ));
        }
        runs.sort(Comparator.comparingDouble((EdgeRun r) -> r.line).thenComparingDouble(r -> r.start));

        List<HorizontalSegment> segments = new ArrayList<>();
        for (EdgeRun run : runs) {
            if (!segments.isEmpty()) {
                HorizontalSegment last = segments.get(segments.size() - 1);
                if (same(last.line, run.line) && same(last.end, run.start)) {
                    last.end = run.end;
                    continue;
                }
            }
            segments.add(new HorizontalSegment(run.line, run.start, run.end));
        }
        return segments;
    }

    private static List<VerticalSegment> collectVerticalSegments(List<MazeWallGrid.WallCell> cells,
                                                                 MazeWallGrid wallGrid,
                                                                 boolean left) {
        List<EdgeRun> runs = new ArrayList<>();
        for (MazeWallGrid.WallCell cell : cells) {
            boolean exposed = left ? wallGrid.isLeftEdgeExposed(cell) : wallGrid.isRightEdgeExposed(cell);
            if (!exposed) {
                continue;
            }
            runs.add(new EdgeRun(
                left ? cell.getX() : cell.getX() + cell.getWidth(),
                cell.getY(),
                cell.getY() + cell.getHeight()
            ));
        }
        runs.sort(Comparator.comparingDouble((EdgeRun r) -> r.line).thenComparingDouble(r -> r.start));

        List<VerticalSegment> segments = new ArrayList<>();
        for (EdgeRun run : runs) {
            if (!segments.isEmpty()) {
                VerticalSegment last = segments.get(segments.size() - 1);
                if (same(last.line, run.line) && same(last.end, run.start)) {
                    last.end = run.end;
                    continue;
                }
            }
            segments.add(new VerticalSegment(run.line, run.start, run.end));
        }
        return segments;
    }

    private static boolean same(float a, float b) {
        return Math.abs(a - b) <= 0.01f;
    }

    private static void drawHorizontalEdge(GdxDrawContext gdx, int[] atlasCoord,
                                           float startX, float endX, float y) {
        float x = startX;
        while (x < endX - 0.001f) {
            float drawWidth = Math.min(WALL_TILE_DRAW_SIZE, endX - x);
            drawAtlasTile(gdx, atlasCoord, x, y, drawWidth, WALL_TILE_DRAW_SIZE);
            x += WALL_TILE_DRAW_SIZE;
        }
    }

    private static void drawVerticalEdge(GdxDrawContext gdx, int[] atlasCoord,
                                         float x, float startY, float endY) {
        float y = startY;
        while (y < endY - 0.001f) {
            float drawHeight = Math.min(WALL_TILE_DRAW_SIZE, endY - y);
            drawAtlasTile(gdx, atlasCoord, x, y, WALL_TILE_DRAW_SIZE, drawHeight);
            y += WALL_TILE_DRAW_SIZE;
        }
    }

    private static void drawAtlasTile(GdxDrawContext gdx, int[] atlasCoord,
                                      float x, float y, float w, float h) {
        int srcX = atlasCoord[0] * (WALL_TILE_SRC_SIZE + WALL_TILE_SPACING);
        int srcY = atlasCoord[1] * (WALL_TILE_SRC_SIZE + WALL_TILE_SPACING);
        gdx.drawTextureRegion(
            WALL_TILESET_ASSET,
            srcX,
            srcY,
            WALL_TILE_SRC_SIZE,
            WALL_TILE_SRC_SIZE,
            x,
            y,
            w,
            h,
            false);
    }

    private static final class EdgeRun {
        private final float line;
        private final float start;
        private final float end;

        private EdgeRun(float line, float start, float end) {
            this.line = line;
            this.start = start;
            this.end = end;
        }
    }

    private static final class HorizontalSegment {
        private final float line;
        private final float start;
        private float end;

        private HorizontalSegment(float line, float start, float end) {
            this.line = line;
            this.start = start;
            this.end = end;
        }
    }

    private static final class VerticalSegment {
        private final float line;
        private final float start;
        private float end;

        private VerticalSegment(float line, float start, float end) {
            this.line = line;
            this.start = start;
            this.end = end;
        }
    }
}
