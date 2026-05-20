# Software Maintenance Report: Paint Application
**Name:** Nahid (BSSE1437)

## 1. Task: SCROLL
**Complaint:** Scrollbars appear incorrectly after drawing outside canvas

### Prompt(s) Used
**Initial:** "When I draw outside the canvas in this Java Swing paint app, scrollbars appear but they look weird and have visual artifacts. The canvas doesn't clear properly. Why does this happen?"
**Refined:** "The PaintCanvas class uses `g.fillRect((int)clipBounds.getX(), (int)clipBounds.getX(), ...)` to clear the background. Does this explain why scrollbars appear incorrectly or leave trails when scrolling out of bounds?"

### LLM Model Used
- ChatGPT 4o
- Gemini 1.5 Pro
- Claude 3.5 Sonnet

### Why the Issue Happened
The root cause was twofold. First, the background clearing logic inside `PaintCanvas.paintComponent` was using `clipBounds.getX()` for both the X and Y coordinates (`g.fillRect((int)clipBounds.getX(), (int)clipBounds.getX(), ...)`). When a scrollbar appeared and the user scrolled vertically, `clipBounds.getX()` remained 0, causing the newly exposed region to not be cleared and leaving visual artifacts that distorted the canvas. 
Second, the `PaintCanvas` did not update its `preferredSize` dynamically, so `JScrollPane` didn't properly show or update scrollbars when strokes extended beyond the initial dimensions. 

### How the AI Solved It
The AIs pointed out the typo in `fillRect`, suggesting to change the second argument to `clipBounds.getY()`. Additionally, they suggested overriding `getPreferredSize()` in `PaintCanvas` to calculate the maximum bounds of all objects dynamically, and calling `revalidate()` inside `addPaintObject()` to alert the `JScrollPane` to update.

### Where the Fix Was Applied
- `PaintCanvas.java`
- Methods modified:
  - `paintComponent()`
  - `addPaintObject()`, `clear()`, `undo()` (added `revalidate()`)
  - Added new overridden method `getPreferredSize()`

### Number of Lines Changed
26 lines added
1 line modified

### Testing Process
1. Drew lines extending past the bottom right of the default canvas.
2. Verified that scrollbars appeared correctly.
3. Scrolled up and down and verified there were no graphical artifacts or duplicated strokes.

### Compare Different LLM Outputs
ChatGPT correctly identified the `clipBounds.getX()` typo right away but didn't address the dynamic size calculation. Gemini provided the complete architectural fix, overriding `getPreferredSize` and using `revalidate()`. Claude suggested a slightly less efficient fix involving listening to mouse events.

### Best Solution
Gemini provided the best solution because it addressed both the visual artifact bug (`getY()`) and the underlying `JScrollPane` layout update mechanism correctly.

---

## 2. Task: YELLOW
**Complaint:** Yellow color cannot be selected

### Prompt(s) Used
**Initial:** "In my Java paint application, I cannot make the color yellow using the RGB sliders. When I try, it turns white instead. Why?"

### LLM Model Used
- ChatGPT 4o
- Gemini 1.5 Pro

### Why the Issue Happened
The `PaintWindow.java` class possessed a `ChangeListener` for its RGB sliders. When reconstructing the `Color` object, it mistakenly used the value of the green slider (`gSlider`) for both the Green and Blue channels: `new Color(rSlider.getValue(), gSlider.getValue(), gSlider.getValue())`. Because yellow requires maximum Red and Green and zero Blue, setting Red and Green to 255 forced Blue to 255 as well, resulting in white.

### How the AI Solved It
The AIs simply scanned the slider event listener and pointed out the typo. The solution was to replace the third argument with `bSlider.getValue()`.

### Where the Fix Was Applied
- `PaintWindow.java`
- Methods modified:
  - `colorChangeListener.stateChanged()`

### Number of Lines Changed
1 line modified

### Testing Process
1. Ran the application.
2. Moved the Red and Green sliders to maximum (255) and the Blue slider to 0.
3. Verified the stroke color successfully turned yellow instead of white.

### Compare Different LLM Outputs
Both ChatGPT and Gemini instantly spotted the typo. Both gave exactly the same, correct fix. 

### Best Solution
Both were equally good for this simple syntax typo.

---

## 3. Task: UNDO
**Complaint:** Undo button sometimes fails

### Prompt(s) Used
**Initial:** "The undo button in my PaintCanvas class updates the `paintObjects` vector to the previous history state, but the UI doesn't always reflect the change immediately. It only updates when I hover the mouse."

### LLM Model Used
- ChatGPT 4o
- Claude 3.5 Sonnet

### Why the Issue Happened
The `undo()` method inside `PaintCanvas` properly restored the `paintObjects` vector from the `history` stack but forgot to invoke `repaint()`. Because of this, the Swing Event Dispatch Thread was never notified that the canvas needed to be redrawn, making it seem like the undo action failed until another action (like moving the mouse) forced a UI refresh.

### How the AI Solved It
The AIs identified the missing UI refresh call and instructed to add `repaint()` at the very end of the `undo()` method in `PaintCanvas.java`.

### Where the Fix Was Applied
- `PaintCanvas.java`
- Methods modified:
  - `undo()`

### Number of Lines Changed
1 line added

### Testing Process
1. Drew 3 distinct shapes.
2. Clicked Undo without moving the mouse onto the canvas.
3. Verified that the most recent shape instantly disappeared.

### Compare Different LLM Outputs
Claude provided a more thorough explanation of the Swing threading model and why `repaint()` is necessary, whereas ChatGPT provided a quick, direct code fix.

### Best Solution
Claude was the most helpful as it explained the underlying "why" in relation to Swing's rendering cycle.

---

## 4. Task: LINE
**Complaint:** Line tool radio button exists but tool not implemented

### Prompt(s) Used
**Initial:** "I have a paint app that has Pencil and Eraser tools, but the Line tool button does nothing. How do I implement the Line tool class and link it to the button?"

### LLM Model Used
- ChatGPT 4o
- Gemini 1.5 Pro

### Why the Issue Happened
The `JRadioButton` for the Line tool was just instantiated with text `new JRadioButton("Line")` and not bound to any `AbstractAction` like the Pencil and Eraser were. Furthermore, there was no `LinePaint` class created to handle rendering straight lines.

### How the AI Solved It
The AIs suggested creating a `LinePaint.java` class that extends `PaintObject`. The new class overrides `paint()` to draw a straight line between the first and last recorded points (`getStartX(), getStartY()` to `getEndX(), getEndY()`). Then, they instructed to create a `lineAction` inside `Actions.java` to set the constructor to `LinePaint.class`, and finally bind this action to the `lineButton` in `PaintWindow.java`.

### Where the Fix Was Applied
- `LinePaint.java` (New file created)
- `Actions.java`
  - Constructor modified
- `PaintWindow.java`
  - Constructor modified

### Number of Lines Changed
35 lines added (New class)
8 lines added to `Actions.java`
1 line modified in `PaintWindow.java`

### Testing Process
1. Launched the app and selected the "Line" radio button.
2. Clicked, dragged, and released the mouse on the canvas.
3. Verified a straight line was drawn from the click point to the release point.

### Compare Different LLM Outputs
ChatGPT recommended implementing a new `PaintObject` subclass from scratch. Gemini correctly noticed that I could have extended `PencilPaint` but agreed that making a standalone `LinePaint` was cleaner since the bounding box math was slightly different.

### Best Solution
ChatGPT's solution was direct and clean, preventing messy inheritance.

---

## 5. Task: THICKNESS
**Complaint:** Add brush thickness control

### Prompt(s) Used
**Initial:** "My PaintObjectConstructor supports a `setThickness()` method, but there is no UI control for it. How do I add a slider to my PaintWindow to control brush thickness?"

### LLM Model Used
- Claude 3.5 Sonnet
- Gemini 1.5 Pro

### Why the Issue Happened
This was a missing feature. The backend logic for thickness was already implemented in the `PaintObject` classes and `PaintObjectConstructor`, but the UI components were absent.

### How the AI Solved It
The AI suggested declaring a new `JSlider` and `JPanel` for thickness control in `PaintWindow.java`. It instructed to set up a `ChangeListener` that reads the slider's value and calls `objectConstructor.setThickness()`. The panel was then added to the `controlPanel` GridBagLayout.

### Where the Fix Was Applied
- `PaintWindow.java`
- Methods modified:
  - Added fields and initialized them in constructor.

### Number of Lines Changed
18 lines added

### Testing Process
1. Started the application and observed the new "Thickness" slider.
2. Drew a line with default thickness.
3. Dragged the slider to the maximum value and drew another line.
4. Verified the second line was significantly thicker.

### Compare Different LLM Outputs
Both Claude and Gemini gave similar Swing code. Gemini provided precise GridBagConstraints adjustments to make the UI look clean alongside the existing panels. 

### Best Solution
Gemini provided the best solution because it paid attention to the GridBagLayout constraints, ensuring the new slider didn't break the existing UI structure.
