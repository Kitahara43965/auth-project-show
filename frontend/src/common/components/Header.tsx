import Link from "next/link";
import Logo from "@/src/common/components/Logo";
import LogoutButton from "@/src/auth/components/LogoutButton";
import { getServerUserResponse } from "@/src/auth/services/get-server-user-response.service";


export default async function Header() {

    const userResponse = await getServerUserResponse();

    return (
        <header className="flex items-center justify-between px-6 py-3 border-b">

            <Link href="/">
                <Logo />
            </Link>

            <nav className="flex items-center gap-4">

                {userResponse ? (
                    <>
                        <LogoutButton />
                        
                        <Link href="/dashboard">
                            ダッシュボード
                        </Link>
                    </>
                ) : (
                    <>
                        <Link href="/login">
                            ログイン
                        </Link>

                        <Link href="/register">
                            会員登録
                        </Link>
                    </>
                )}

            </nav>

        </header>
    );
}