package com.lunartag.app.services;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * An optional Accessibility Service to help automate sending photos through WhatsApp.
 * The user must enable this service manually in the device's accessibility settings.
 *
 * NOTE: This is a complex and potentially fragile feature, as it relies on the
 * structure of the WhatsApp UI, which can change.
 */
public class LunarTagAccessibilityService extends AccessibilityService {

    private static final String TAG = "AccessibilityService";
    private static final String WHATSAPP_PACKAGE_NAME = "com.whatsapp";

    // --- State management for the automation flow ---
    private static boolean isServiceActive = false;
    private static String targetGroupName = null;

    /**
     * This method is called by the SendService to "arm" the accessibility service
     * just before the WhatsApp share intent is launched.
     * @param groupName The exact name of the target WhatsApp group.
     */
    public static void activate(String groupName) {
        targetGroupName = groupName;
        isServiceActive = true;
        Log.d(TAG, "Accessibility service activated for group: " + groupName);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (!isServiceActive || targetGroupName == null) {
            return;
        }

        // Only react to events from WhatsApp
        if (event.getPackageName() == null || !event.getPackageName().toString().equals(WHATSAPP_PACKAGE_NAME)) {
            return;
        }

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) {
            return;
        }

        // --- Step 1: Find the target group in the contact list and click it ---
        // This is the most complex step. The ideal way is to find a search icon, click it,
        // type the group name, and then click the result. A simpler fallback is to
        // just search the current screen for the group name.
        List<AccessibilityNodeInfo> groupNodes = rootNode.findAccessibilityNodeInfosByText(targetGroupName);
        if (groupNodes != null && !groupNodes.isEmpty()) {
            for (AccessibilityNodeInfo node : groupNodes) {
                // We need to find the clickable parent to click on the list item
                AccessibilityNodeInfo parent = node.getParent();
                while (parent != null) {
                    if (parent.isClickable()) {
                        Log.d(TAG, "Found clickable group node. Clicking it.");
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        // We assume this will take us to the next screen.
                        return;
                    }
                    parent = parent.getParent();
                }
            }
        }

        // --- Step 2: Find the "Send" button and click it ---
        // The send button in WhatsApp often has a content description like "Send".
        List<AccessibilityNodeInfo> sendButtonNodes = rootNode.findAccessibilityNodeInfosByText("Send");
        if (sendButtonNodes != null && !sendButtonNodes.isEmpty()) {
            for (AccessibilityNodeInfo node : sendButtonNodes) {
                if (node.isClickable()) {
                    Log.d(TAG, "Found 'Send' button. Clicking it.");
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);

                    // Deactivate the service after the action is performed to prevent accidental clicks.
                    isServiceActive = false;
                    targetGroupName = null;
                    Log.d(TAG, "Accessibility service deactivated.");
                    return;
                }
            }
        }
        
        // Clean up the node info
        rootNode.recycle();
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "Accessibility service interrupted.");
        isServiceActive = false;
        targetGroupName = null;
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "Accessibility service connected.");
    }
                }
