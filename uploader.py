import os
import requests
import json

# Per-mod: Update this for each mod!!!

MOD_ID = "lithostitched"
MOD_VERSION = "1.8.0"
CHANGELOG = """
Lithostitched 1.8.0 is largely focused on backend changes, with the codebase split up based on version again. Various other changes and fixes have made it in, though.

**Additions**

- `add_spawn_costs` modifier (adds spawn costs for mobs in given biome(s))
- `set_tree_decorators` modifier (adds/overrides tree decorators on given tree feature(s))
- `branched_mega_jungle` foliage placer (for wider mega jungle trees)
- `branched_mega_pine` foliage placer (for wider pine jungle trees)
- `large_mangrove` root placer (for root placements with 2x2 trees)
- `cellular` density function type (in-house 2d cellular noise with more consistency/features than fast noise)
- Introduced an optimization to density function caching. This will be most noticeable when playing with heavy worldgen packs like Tectonic or Lithosphere.
    - A special thank you to Unnecessarymb and Evanbones for finding and implementing this optimization, respectively.

**Fixes**

- Fixed a critical issue that caused worlds with surface rule injections to corrupt under certain conditions, such as when having a world with both RU 0.6 and Terrablender installed.
- Fixed the fields on the `offset` placement modifier being non-optional.
- Fixed the `dungeon` feature type not working.
- Fixed annoying but harmless log errors when World Weaver is installed.
- Fix the assumed minimum value in various places (e.g. `sample_density` placement condition) being 0, not roughly -1.8E308.
"""
UPLOAD_VERSIONS = [
    ("fabric", "26.1"),
    ("neoforge", "26.1"),
]

DEPENDENCIES = []

MODRINTH_ID = "XaDC71GB"
CURSEFORGE_ID = "936015"

RELEASE_TYPE = "release"

# Global: Should usually not be touched!

BASE_FOLDER = os.path.dirname(os.path.abspath(__file__))


MODRINTH_TOKEN = os.getenv('TOKEN_MR')
if not MODRINTH_TOKEN:
    raise EnvironmentError("MODRINTH_TOKEN is unset!")
MODRINTH_GAME_VERSIONS = {
    "21.1": ["1.21.1"],
    "26.1": ["26.1", "26.1.1", "26.1.2"],
    "26.2": ["26.2"]
}

CURSEFORGE_TOKEN = os.getenv('TOKEN_CF')
if not CURSEFORGE_TOKEN:
    raise EnvironmentError("CURSEFORGE_TOKEN is unset!")
CURSEFORGE_URL = f"https://minecraft.curseforge.com/api/v1/projects/{CURSEFORGE_ID}/upload-file"
CURSEFORGE_GAME_VERSIONS = {
    "21.1": [11779],
    "26.1": [15933, 16021, 16082],
    "26.2": [16498],
}
CURSEFORGE_LOADERS = {
    "fabric": 7499,
    "forge": 7498,
    "neoforge": 10150,
}


# Code

def upload_modrinth(loader: str, version: str, file_path: str, dependencies):

    game_versions = MODRINTH_GAME_VERSIONS.get(version)

    metadata = {
        "name": f"v{MOD_VERSION} ~ {loader.title()} {version}",
        "version_number": f"{MOD_VERSION}-{loader}-{version}",
        "project_id": MODRINTH_ID,
        "game_versions": game_versions,
        "loaders": [loader],
        "featured": True,
        "changelog": CHANGELOG,
        "version_type": RELEASE_TYPE,
        "file_parts": ["file"],
        "dependencies": dependencies
    }

    with open(file_path, 'rb') as mod_file:
        response = requests.post(
            "https://api.modrinth.com/v2/version",
            headers={
                "Authorization": MODRINTH_TOKEN
            },
            files={
                'file': (os.path.basename(file_path), mod_file, 'application/java-archive'),
            },
            data={
                'data': json.dumps(metadata)
            }
        )

        name = f"MR {loader.title()} {version}: "

        if response.status_code == 200:
            print(name + "Success")
            print(response.json())
        else:
            print(name + f"Failed ({response.status_code})")
            print(response.text)


def upload_curseforge(loader: str, version: str, file_path: str, dependencies):
    headers = {
        "X-Api-Token": CURSEFORGE_TOKEN
    }

    # Lookup version and loader IDs
    game_version_ids = CURSEFORGE_GAME_VERSIONS.get(version)
    modloader_id = CURSEFORGE_LOADERS.get(loader)

    if not game_version_ids or not modloader_id:
        print(f"Skipping CurseForge upload for {loader} {version}: unknown IDs")
        return

    # Metadata
    metadata = {
        "displayName": f"v{MOD_VERSION} ~ {loader.title()} {version}",
        "gameVersionNames": ["Server", "Client"],
        "gameVersions": game_version_ids + [modloader_id],
        "releaseType": RELEASE_TYPE,
        "changelog": CHANGELOG,
        "changelogType": "markdown",
        "dependencies": dependencies
    }
    metastr = json.dumps(metadata)

    with open(file_path, "rb") as mod_file:
        files = {
            "file": (os.path.basename(file_path), mod_file, "application/java-archive")
        }

        response = requests.post(
            f"https://minecraft.curseforge.com/api/projects/{CURSEFORGE_ID}/upload-file",
            headers=headers,
            files=files,
            data={"metadata": metastr},
            auth=("Apollo", CURSEFORGE_TOKEN)
        )

        name = f"CF {loader.title()} {version}: "

        if response.status_code == 200:
            print(name + "Success")
            print(response.json())
        else:
            print(name + f"Failed ({response.status_code})")
            print(response.text)


for modloader, game_version in UPLOAD_VERSIONS:
    mod_path = os.path.join(
        BASE_FOLDER,
        'versions',
        modloader,
        'build',
        'libs',
        f'{MOD_ID}-{MOD_VERSION}-{modloader}-{game_version}.jar'
    )

    dependencies = DEPENDENCIES.copy()
    if (modloader == "fabric"):
        dependencies.append(
            {
                "mod_name": "Fabric API",
                "modId": 306612,
                "relationType": 3,
                "project_id": "P7dR8mSH",
                "dependency_type": "required"
            }
        )

    if not os.path.exists(mod_path):
        print(f"File not found, skipping: {mod_path}")
        continue

    upload_modrinth(modloader, game_version, mod_path, dependencies)
    upload_curseforge(modloader, game_version, mod_path, dependencies)

input("Press any key to close")