# Configuration file for the Sphinx documentation builder.
#
# For the full list of built-in configuration values, see the documentation:
# https://www.sphinx-doc.org/en/master/usage/configuration.html

project = ""
copyright = "2024, Christian Oliverio"
author = "Christian Oliverio"

extensions = [
    "sphinx.ext.autodoc",
    "myst_parser",
    "sphinx_inline_tabs",
    "sphinx.ext.githubpages",
]

sphinx_tabs_valid_builders = ["linkcheck"]

source_suffix = [".rst", ".md"]

templates_path = ["_templates"]
exclude_patterns = []

html_favicon = "ico/robovikes.ico"

html_theme = "furo"
html_title = ""
html_static_path = ["_static"]

html_css_files = ["theme_robovikes.css"]

html_theme_options = {
    "light_logo": "images/robovikes-light.png",
    "dark_logo": "images/robovikes-dark.png",
    "sidebar_hide_name": True,
    "source_repository": "https://github.com/frc4206/battleaid",
    "light_css_variables": {
        "color-brand-primary": "#031db0",
        "color-brand-content": "#031db0",
    },
    "dark_css_variables": {
        "color-brand-primary": "#366aca",
        "color-brand-content": "#366aca",
    },
}

myst_enable_extensions = ["colon_fence"]

highlight_options = {"linenothreshold": 1}

# enables latex support
mathjax_path = "https://cdn.jsdelivr.net/npm/mathjax@3/es5/tex-mml-chtml.js"
