package ure.ui;

import ure.render.URenderer;
import ure.sys.UCommander;

import javax.inject.Inject;
import java.lang.ref.WeakReference;
import java.util.LinkedHashSet;
import java.util.Set;

public class View {

    @Inject
    public URenderer renderer;
    @Inject
    public UCommander commander;

    public int x, y, width, height;

    protected LinkedHashSet<View> children = new LinkedHashSet<>();
    protected WeakReference<View> parent = new WeakReference<>(null);

    protected boolean clipsToBounds = false;
    protected int[] clipRectBuffer = new int[4];

    /**
     * Set this view's position and dimensions.
     * @param x The x coordinate for the left side of this view.
     * @param y The y coordinate for the top of this view.
     * @param width The view's width, in pixels.
     * @param height The view's height, in pixels.
     */
    public void setBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Provides the set of views that will be rendered within this view.  Those child views may have children of their own that won't be included.
     * @return Set<View> populated with all direct children of this view.
     */
    public Set<View> children() {
        return children;
    }

    /**
     * Adds a child view that will be rendered within this view's bounds.
     * @param view The child view to add.
     */
    public void addChild(View view) {
        children.add(view);
        view.setParent(this);
    }

    /**
     * Removed a child view from this view.
     * @param view The child view to remove.
     */
    public void removeChild(View view) {
        children.remove(view);
    }

    /**
     * Sets this view's parent view, for cases when we need to climb the view hierarchy.  The parent will
     * be referenced weakly to prevent circular references.
     * @param view The view that contains this view.  May be null if this is a root view.
     */
    public void setParent(View view) {
        this.parent = new WeakReference<>(view);
    }

    /**
     * Gets the view that contains this view, if any.
     * @return The parent view, or null if this view is at the root.
     */
    public View getParent() {
        return this.parent.get();
    }

    /**
     * Gets this view's X coordinate, in pixels, relative to its parent view.
     * @return The X coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * Gets this view's Y coordinate, in pixels, relative to its parent view.
     * @return The Y coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * Gets this view's width in pixels.
     * @return The width in pixels.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets this view height in pixels.
     * @return The height in pixels.
     */
    public int getHeight() {
        return height;
    }

    /**
     * This isn't used by the view directly, but is provided for the renderer as a place to store the clip
     * rectangle to restore after rendering this view.  This is done to avoid allocating objects when rendering.
     * @return the clip rect buffer
     */
    public int[] getClipRectBuffer() {
        return clipRectBuffer;
    }

    /**
     * Gets this view's X position in the root view's coordinate system.
     * @return The absolute X position.
     */
    public int absoluteX() {
        int absoluteX = x;
        View parent = getParent();
        while (parent != null) {
            absoluteX += parent.getX();
            parent = parent.getParent();
        }
        return absoluteX;
    }

    /**
     * Gets this view's Y position in the root view's coordinate system.
     * @return The absolute Y position.
     */
    public int absoluteY() {
        int absoluteY = y;
        View parent = getParent();
        while (parent != null) {
            absoluteY += parent.getY();
            parent = parent.getParent();
        }
        return absoluteY;
    }

    public int mouseX() {
        return commander.mouseX() - absoluteX();
    }

    public int mouseY() {
        return commander.mouseY() - absoluteY();
    }

    /**
     * Draw this view.  The renderer will handle drawing any child views.
     */
    public void draw() {
        // Do any drawing required for this view
    }

    public boolean clipsToBounds() { return clipsToBounds; }
    public void setClipsToBounds(boolean clipsToBounds) { this.clipsToBounds = clipsToBounds; }

    public String xystr() { return "my xy is " + Integer.toString(x) + "," + Integer.toString(y); }
    public String absxystr() { return "abs xy is " + Integer.toString(absoluteX()) + "," + Integer.toString(absoluteY()) + " " + Integer.toString(width) + "x" + Integer.toString(height); }

    public boolean isMouseInside() {
        int mousex = commander.mouseX();
        int mousey = commander.mouseY();
        if (mousex >= absoluteX() && mousex < absoluteX()+width && mousey >= absoluteY() && mousey < absoluteY()+height) {
            return true;
        }
        return false;
    }
}
