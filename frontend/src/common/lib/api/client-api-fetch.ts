export async function clientApiFetch(
    url: string,
    options: RequestInit = {}
) {

    console.log("🔥 clientApiFetch START", url);

    let response =
        await fetch(
            url,
            {
                ...options,
                credentials:"include",
            }
        );

    console.log("🔥 first response status",response.status);


    if(response.status === 401){

        console.log("🔥 refresh start");

        const refreshResponse =
            await fetch(
                "/api/refresh",
                {
                    method:"POST",
                    credentials:"include",
                }
            );


        console.log("🔥 refresh response",refreshResponse.status);


        if(refreshResponse.ok){

            response =
                await fetch(
                    url,
                    {
                        ...options,
                        credentials:"include",
                    }
                );

            console.log("🔥 after refresh[OK]",response.status);
        }
    }

    return response;
}