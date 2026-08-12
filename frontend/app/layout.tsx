import "./globals.css";
import Header from "@/src/common/components/Header";
import AuthBroadcastChannelProvider from "@/src/auth/components/AuthBroadcastChannelProvider";

export default async function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {

  return (
    <html lang="ja">
      <body>
        <AuthBroadcastChannelProvider>
          <Header />
          {children}
        </AuthBroadcastChannelProvider>
      </body>
    </html>
  );
}