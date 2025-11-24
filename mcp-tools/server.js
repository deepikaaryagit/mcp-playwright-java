const express = require("express");
const bodyParser = require("body-parser");

const app = express();
app.use(bodyParser.json());

// ALWAYS RETURN JSON OBJECT
app.post("/tools/suggestLocator", (req, res) => {
  try {
    const html = req.body.html || "";

    // Extract: id="username"
    const idMatch = html.match(/id=["'](.*?)["']/);
    if (idMatch) {
      return res.json({ output: `#${idMatch[1]}` });
    }

    // Extract: name="username"
    const nameMatch = html.match(/name=["'](.*?)["']/);
    if (nameMatch) {
      return res.json({ output: `[name='${nameMatch[1]}']` });
    }

    // fallback → still JSON
    return res.json({ output: "input" });

  } catch (err) {
    return res.json({ error: err.message });
  }
});

// ========================================
// Tool 2: analyzeFailure
// ========================================
app.post("/tools/analyzeFailure", (req, res) => {
  try {
    const body = req.body || {};
    const testName = body.testName || "Unknown test";
    const errorMessage = body.errorMessage || "";
    const lastStep = body.lastStep || "Unknown step";

    let summary = `Test "${testName}" failed at step: ${lastStep}`;
    let rootCause = "Root cause could not be determined.";
    let suggestions = [];

    // --- Simple rule-based analysis for now ---

    if (errorMessage.includes("strict mode violation")) {
      rootCause =
        "Playwright strict mode found multiple elements for the same locator.";
      suggestions.push(
        "Use a more specific locator (e.g. add [data-testid], text filter, or nth()).",
        "Check that your locator uniquely identifies a single element.",
        "If there are multiple matching fields, consider using page.getByLabel() or page.getByRole()."
      );
    } else if (errorMessage.includes("Timeout 30000ms exceeded")
            && errorMessage.includes("waiting for locator")) {
      rootCause =
        "The element never became ready within the timeout while waiting for a locator.";
      suggestions.push(
        "Verify that the locator is correct on the current page state.",
        "Check for animations, overlays, or modals blocking the element.",
        "Add an explicit wait for networkidle or for the overlay to disappear."
      );
    } else if (errorMessage.toLowerCase().includes("navigation timeout")) {
      rootCause = "Page navigation took too long or never finished.";
      suggestions.push(
        "Verify the URL and environment (QA vs Dev).",
        "Check for backend slowness or network issues.",
        "Increase timeout or wait for networkidle before further steps."
      );
    } else if (errorMessage.toLowerCase().includes("not visible")
            || errorMessage.toLowerCase().includes("element is not visible")) {
      rootCause = "Element was found in the DOM but was not visible or clickable.";
      suggestions.push(
        "Scroll into view before interacting.",
        "Ensure no modal, toast, or overlay is covering the element.",
        "Wait for element to be visible and enabled."
      );
    } else {
      rootCause =
        "No specific rule matched this error. Needs manual investigation.";
      suggestions.push(
        "Check the full stack trace and HTML snapshot at failure time.",
        "Verify the locator and the page state where the failure occurred."
      );
    }

    const analysis =
      summary +
      "\n\nAI Root Cause:\n" +
      rootCause +
      "\n\nSuggested Fixes:\n- " +
      suggestions.join("\n- ");

    return res.json({ output: analysis });
  } catch (err) {
    return res.json({ error: err.message });
  }
});


app.listen(3001, () => {
  console.log("🟢 MCP Server running at http://localhost:3001");
});