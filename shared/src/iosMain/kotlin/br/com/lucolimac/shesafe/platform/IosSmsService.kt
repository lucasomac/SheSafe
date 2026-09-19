package br.com.lucolimac.shesafe.platform

import kotlinx.cinterop.ExperimentalForeignApi
import platform.MessageUI.MFMessageComposeViewController
import platform.MessageUI.MFMessageComposeViewControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
class IosSmsService : SmsService {
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

    private class Delegate(
        private val onResult: (SmsResult) -> Unit,
    ) : NSObject(), MFMessageComposeViewControllerDelegateProtocol {
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

    private fun topViewController(): UIViewController? {
        var controller = UIApplication.sharedApplication.keyWindow?.rootViewController
        while (controller?.presentedViewController != null) {
            controller = controller.presentedViewController
        }
        return controller
    }
}
