from packaging import version
from funasr import __version__  # Ensure that __version__ is defined in your package's __init__.py


def get_pypi_version(package_name):
    import requests

    url = f"https://pypi.org/pypi/{package_name}/json"
    response = requests.get(url)
    if response.status_code == 200:
        data = response.json()
        return version.parse(data["info"]["version"])
    else:
        raise Exception("Failed to retrieve version information from PyPI.")


def check_for_update(disable=False):
    current_version = version.parse(__version__)
    print(f"funasr version: {current_version}.")
    if disable:
        return

    print(
        "Check update of funasr, and it would cost few times. You may disable it by set `disable_update=True` in AutoModel"
    )
    pypi_version = get_pypi_version("funasr")

    if current_version < pypi_version:
        print(f"New version is available: {pypi_version}.")
        print('Please use the command "pip install -U funasr" to upgrade.')
    else:
        print(f"You are using the latest version of funasr-{current_version}")
