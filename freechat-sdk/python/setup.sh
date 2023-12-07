#!/bin/bash

set -eux

ver=$(sed -n 's/__version__ = "\([a-zA-Z0-9.-]\{1,\}\)"/\1/p' freechat-sdk/__init__.py | head -1)

python3 setup.py clean
python3 setup.py bdist_wheel
python3 setup.py sdist

twine check dist/freechat_sdk-${ver}-py3-none-any.whl
twine upload dist/freechat_sdk-${ver}-py3-none-any.whl --verbose