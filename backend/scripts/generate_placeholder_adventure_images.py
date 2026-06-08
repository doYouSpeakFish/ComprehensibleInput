#!/usr/bin/env python3
"""Generate placeholder adventure cover images.

These are intentionally simple solid-colour PNGs that stand in for the real, AI-generated artwork
that will replace them later. Each image in the catalogue gets its own deterministic colour so the
placeholders are visually distinct, but nothing should depend on the specific colour of any image.

The image ids are read from the Kotlin catalogue so this script can never drift out of sync with it.
Run from the repository root (or anywhere):

    python3 backend/scripts/generate_placeholder_adventure_images.py
"""
from __future__ import annotations

import hashlib
import pathlib
import re
import struct
import zlib

REPO_ROOT = pathlib.Path(__file__).resolve().parents[2]
CATALOGUE = REPO_ROOT / "backend/src/main/kotlin/input/comprehensible/backend/textadventure/AdventureImageCatalog.kt"
OUTPUT_DIR = REPO_ROOT / "backend/src/main/resources/adventure-images"

WIDTH = 1024
HEIGHT = 576


def read_image_ids() -> list[str]:
    text = CATALOGUE.read_text(encoding="utf-8")
    # Match the `id = "..."` argument of each AdventureImage(...) entry.
    ids = re.findall(r'id\s*=\s*"([a-z0-9-]+)"', text)
    # De-duplicate while preserving order (the file also defines other id-like constants we skip via
    # the AdventureImage block ordering; the catalogue ids are the ones inside the images list).
    seen: dict[str, None] = {}
    for image_id in ids:
        seen.setdefault(image_id, None)
    return list(seen.keys())


def colour_for(image_id: str) -> tuple[int, int, int]:
    digest = hashlib.sha256(image_id.encode("utf-8")).digest()
    # Keep the colours fairly light so any future overlaid text would be legible, and distinct.
    r = 80 + digest[0] % 150
    g = 80 + digest[1] % 150
    b = 80 + digest[2] % 150
    return r, g, b


def png_bytes(width: int, height: int, colour: tuple[int, int, int]) -> bytes:
    def chunk(tag: bytes, data: bytes) -> bytes:
        return (
            struct.pack(">I", len(data))
            + tag
            + data
            + struct.pack(">I", zlib.crc32(tag + data) & 0xFFFFFFFF)
        )

    r, g, b = colour
    row = b"\x00" + bytes((r, g, b)) * width
    raw = row * height
    ihdr = struct.pack(">IIBBBBB", width, height, 8, 2, 0, 0, 0)
    return (
        b"\x89PNG\r\n\x1a\n"
        + chunk(b"IHDR", ihdr)
        + chunk(b"IDAT", zlib.compress(raw, 9))
        + chunk(b"IEND", b"")
    )


def main() -> None:
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    image_ids = read_image_ids()
    if not image_ids:
        raise SystemExit(f"No image ids found in {CATALOGUE}")
    for image_id in image_ids:
        path = OUTPUT_DIR / f"{image_id}.png"
        path.write_bytes(png_bytes(WIDTH, HEIGHT, colour_for(image_id)))
    print(f"Wrote {len(image_ids)} placeholder images to {OUTPUT_DIR}")


if __name__ == "__main__":
    main()
