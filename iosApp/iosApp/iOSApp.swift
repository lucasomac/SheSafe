import SwiftUI
import GoogleSignIn

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) private var appDelegate

	var body: some Scene {
		WindowGroup {
            ComposeView()
                .onOpenURL { url in
                    _ = GIDSignIn.sharedInstance.handle(url)
                }
		}
	}
}

private final class AppDelegate: NSObject, UIApplicationDelegate {
    func application(
        _ app: UIApplication,
        open url: URL,
        options: [UIApplication.OpenURLOptionsKey: Any] = [:],
    ) -> Bool {
        GIDSignIn.sharedInstance.handle(url)
    }
}