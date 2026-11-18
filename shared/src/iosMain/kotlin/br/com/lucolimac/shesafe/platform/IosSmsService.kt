package br.com.lucolimac.shesafe.platform

import kotlinx.cinterop.ExperimentalForeignApi
import platform.MessageUI.MFMessageComposeViewController
import platform.MessageUI.MFMessageComposeViewControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.darwin.NSObject

/**
 * iOS implementation of [SmsService] presenting Apple's [MFMessageComposeViewController].
 *
 * Because iOS prohibits silent background SMS transmission by third-party apps, messages are
 * pre-populated in the system Messages sheet for explicit user confirmation.
 */
@OptIn(ExperimentalForeignApi::class)
class IosSmsService : SmsService {
    /**
     * Presents [MFMessageComposeViewController] pre-filled with [SmsRequest.recipients] and
     * [SmsRequest.message] when the device supports text messaging.
     *
     * @param request [SmsRequest] containing recipient phone numbers and message body.
     * @param onResult Callback invoked with [SmsResult.Unsupported], [SmsResult.Failed], or
     *   [SmsResult.UserConfirmationRequired] once the composer finishes.
     */
    override fun send(request: SmsRequest, onResult: (SmsResult) -> Unit) {
        if (!MFMessageComposeViewController.canSendText()) {
            onResult(SmsResult.Unsupported)
            return
        }

        val composer = MFMessageComposeViewController()
        composer.recipients = request.recipients
        composer.body = request.message
        composer.messageComposeDelegate = Delegate(onResult)
        topViewController()?.presentViewController(composer, true, null)
            ?: onResult(SmsResult.Failed("Unable to present the Messages composer"))
    }

    /**
     * MessageUI delegate that dismisses the system SMS composer and reports
     * [SmsResult.UserConfirmationRequired] back to the caller.
     *
     * @property onResult Callback receiving the [SmsResult] after composer dismissal.
     */
    private class Delegate(
        private val onResult: (SmsResult) -> Unit,
    ) : NSObject(), MFMessageComposeViewControllerDelegateProtocol {
        /**
         * Dismisses [controller] and notifies [onResult] that iOS user confirmation was required.
         *
         * @param controller Presented [MFMessageComposeViewController] instance.
         * @param didFinishWithResult Native completion status returned by MessageUI.
         */
        override fun messageComposeViewController(
            controller: MFMessageComposeViewController,
            didFinishWithResult: platform.MessageUI.MessageComposeResult,
        ) {
            controller.dismissViewControllerAnimated(true, null)
            // iOS always requires the user to confirm in Messages; unlike Android,
            // the app must not claim silent delivery.
            onResult(SmsResult.UserConfirmationRequired)
        }
    }

    /**
     * Traverses the active key window's presentation chain to locate the topmost [UIViewController].
     *
     * @return Topmost presented [UIViewController], or `null` if no root controller is attached.
     */
    private fun topViewController(): UIViewController? {
        var controller = UIApplication.sharedApplication.keyWindow?.rootViewController
        while (controller?.presentedViewController != null) {
            controller = controller.presentedViewController
        }
        return controller
    }
}
