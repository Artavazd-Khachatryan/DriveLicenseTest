import UIKit
import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            // Compose applies safe-area insets itself; partial ignore misaligns iOS hit targets (e.g. top-bar back).
            .ignoresSafeArea(.all)
            .ignoresSafeArea(.keyboard)
    }
}



